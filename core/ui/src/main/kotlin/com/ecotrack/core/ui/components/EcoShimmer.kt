package com.ecotrack.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.ecotrack.core.ui.util.ecoTouchTarget

@Composable
fun EcoShimmerList(
    itemCount: Int = 5,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(itemCount) {
            EcoShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
            )
        }
    }
}

@Composable
fun EcoShimmerDashboard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EcoShimmerBox(modifier = Modifier.fillMaxWidth().height(120.dp))
        EcoShimmerBox(modifier = Modifier.fillMaxWidth().height(80.dp))
        EcoShimmerBox(modifier = Modifier.fillMaxWidth().height(200.dp))
    }
}

@Composable
fun EcoShimmerBox(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "shimmerAlpha",
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha),
            androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha * 0.5f),
        ),
        start = Offset.Zero,
        end = Offset(400f, 400f),
    )
    Spacer(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(brush),
    )
}

@Composable
fun <T> EcoResourceContent(
    resource: com.ecotrack.core.common.result.Resource<T>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    emptyMessage: String? = null,
    isEmpty: (T) -> Boolean = { false },
    loading: @Composable () -> Unit = { EcoShimmerList() },
    content: @Composable (T) -> Unit,
) {
    when (resource) {
        com.ecotrack.core.common.result.Resource.Loading -> loading()
        is com.ecotrack.core.common.result.Resource.Error -> {
            if (resource.isCritical) {
                EcoError(
                    message = resource.message,
                    onRetry = onRetry,
                    modifier = modifier.fillMaxSize(),
                )
            } else {
                EcoError(
                    message = resource.message,
                    onRetry = onRetry,
                    modifier = modifier.fillMaxSize(),
                )
            }
        }
        is com.ecotrack.core.common.result.Resource.Success -> {
            if (emptyMessage != null && isEmpty(resource.data)) {
                EcoEmpty(message = emptyMessage, modifier = modifier.fillMaxSize())
            } else {
                content(resource.data)
            }
        }
    }
}
