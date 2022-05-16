package com.nucu.dynamiclistcompose.ui.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nucu.dynamiclistcompose.actions.ContextViewAction
import com.nucu.dynamiclistcompose.controllers.DynamicListComposeLoader
import com.nucu.dynamiclistcompose.controllers.DynamicListComposeController
import com.nucu.dynamiclistcompose.actions.DynamicListAction
import com.nucu.dynamiclistcompose.models.DynamicListRequestModel
import com.nucu.dynamiclistcompose.ui.components.ErrorView
import com.nucu.dynamiclistcompose.ui.components.LoaderView
import com.nucu.dynamiclistcompose.viewModels.DynamicListViewModel

class DynamicListCompose(
    requestModel: DynamicListRequestModel
) : DynamicListComposeLoader() {

    private var bodyComposeController: DynamicListComposeController? = null
    private var headerComposeController: DynamicListComposeController? = null

    private val dynamicListRequestModel = mutableStateOf<DynamicListRequestModel?>(requestModel)

    @Composable
    override fun <T: DynamicListComposeController> DynamicListScreen(
        bodyAdapterController: T,
        headerAdapterController: T,
        action: ContextViewAction?
    ) {
        this.bodyComposeController = bodyAdapterController
        this.headerComposeController = headerAdapterController

        DynamicListContent(action = action)
    }

    @Composable
    private fun DynamicListContent(
        dynamicListViewModel: DynamicListViewModel = hiltViewModel(),
        action: ContextViewAction?
    ) {
        val dynamicListState by dynamicListViewModel.dynamicListAction.collectAsState()

        action?.let {
            when (it) {
                is ContextViewAction.Reload -> dynamicListViewModel.load(dynamicListRequestModel.value!!)
            }
        }

        when (dynamicListState) {

            is DynamicListAction.SkeletonAction -> {
                val skeletons = (dynamicListState as DynamicListAction.SkeletonAction).renderTypes
                bodyComposeController?.dispatchSkeletons(skeletons)
                bodyComposeController?.DynamicListSkeletons()
            }

            is DynamicListAction.LoadingAction -> {
                LoaderView()
                dynamicListViewModel.load(dynamicListRequestModel.value!!)
            }

            is DynamicListAction.ErrorAction -> ErrorView((dynamicListState as DynamicListAction.ErrorAction).exception) {
                dynamicListViewModel.load(dynamicListRequestModel.value!!)
            }

            is DynamicListAction.SuccessAction -> {
                val container = (dynamicListState as DynamicListAction.SuccessAction).container
                // Add data to controllers.
                bodyComposeController?.dispatch(container.bodies)
                headerComposeController?.dispatch(container.headers)

                // Show elements.
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    headerComposeController?.ComposeComponent()

                    bodyComposeController?.ComposeComponent()
                }
            }
        }
    }
}
