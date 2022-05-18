package com.nucu.dynamiclistcompose.components.tobacco

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nucu.dynamiclistcompose.R

@Composable
fun TobaccoComponentView(
    coordinates: ((LayoutCoordinates) -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colors.primaryVariant)
            .onGloballyPositioned { coordinates?.invoke(it) }
    ) {
        Text(
            modifier = Modifier
                .padding(10.dp),
            textAlign = TextAlign.Justify,
            text = stringResource(R.string.label_lorem_ipsum),
            color = MaterialTheme.colors.secondary
        )
    }
}

@Composable
@Preview
fun PreviewTobaccoComponentView() {
    TobaccoComponentView()
}