package com.nucu.dynamiclistcompose.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nucu.dynamiclistcompose.listeners.TooltipQueue
import com.nucu.dynamiclistcompose.ui.base.ScrollAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class TooltipQueueImpl @Inject constructor(

): ViewModel(), TooltipQueue {

    private val queue = Stack<ScrollAction.ScrollWithTooltip>()
    private var currentTooltip: ScrollAction.ScrollWithTooltip? = null

    private val _onFinished = MutableStateFlow(false)
    override val onFinished: StateFlow<Boolean> = _onFinished

    private val _tooltipCallback = MutableStateFlow<(() -> Unit)?>(null)
    private val tooltipCallback: StateFlow<(() -> Unit)?> = _tooltipCallback

    private val _show = MutableStateFlow(false)
    private val show: StateFlow<Boolean> = _show

    override fun add(tooltipAction: ScrollAction.ScrollWithTooltip) {
        if (currentTooltip == null) {
            currentTooltip = tooltipAction
            executeCurrentTooltip()
        } else {
            queue.push(tooltipAction)
        }
    }

    override fun hasPendingTransactions(): Boolean {
        return queue.isNotEmpty()
    }

    override fun clear() {
        queue.clear()
        currentTooltip = null
    }

    private fun show() {
        viewModelScope.launch {
            delay(DEFAULT_SHOW_DELAY.toLong())
            _show.value = true
        }
    }

    private fun hide() {
        _show.value = false
    }

    private fun executeCurrentTooltip() {
        currentTooltip?.let {
            val tooltipShowStrategy = it.tooltipShowStrategy

            if (tooltipShowStrategy.firstToHappen == true) {
                showWhenFirstToHappen()
            } else {
                if (tooltipShowStrategy.untilUserInteraction == true) {
                    showUntilUserInteraction()
                } else {
                    showUntilExpiration()
                }
            }
        }
    }

    private fun showWhenFirstToHappen() {
        currentTooltip?.let {

            var isDelayFinished = false

            // Callback to finish
            _tooltipCallback.value = {
                if (isDelayFinished.not()) {
                    _show.value = false
                    executeNext()
                }
            }

            // Show tooltip
            show()

            viewModelScope.launch {
                // Delay time to finish
                delay((it.tooltipShowStrategy.expirationTime + DEFAULT_EXTRA_DURATION).toLong())

                // Hide tooltip
                hide()

                // Set finished to prevent execute next again
                isDelayFinished = true

                // Execute next tooltip
                executeNext()
            }
        }
    }

    private fun showUntilExpiration() {
        currentTooltip?.let {
            // Show tooltip
            show()

            viewModelScope.launch {
                // Delay time to finish
                delay((it.tooltipShowStrategy.expirationTime + DEFAULT_EXTRA_DURATION).toLong())

                // Hide tooltip
                hide()

                // Execute next tooltip
                executeNext()
            }
        }
    }

    private fun showUntilUserInteraction() {
        // Show tooltip
        show()

        // Callback to finish
        _tooltipCallback.value = {

            // Hide tooltip
            hide()

            // Execute next
            executeNext()
        }
    }

    private fun executeNext() {
        if (hasPendingTransactions()) {
            currentTooltip = queue.pop()
            viewModelScope.launch {
                executeCurrentTooltip()
            }
        } else {
            _onFinished.value = true
        }
    }


    companion object {
        const val DEFAULT_EXTRA_DURATION = 100
        const val DEFAULT_SHOW_DELAY = 500
    }
}