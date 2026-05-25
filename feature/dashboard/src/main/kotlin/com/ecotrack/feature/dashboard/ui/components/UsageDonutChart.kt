package com.ecotrack.feature.dashboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.ecotrack.core.design.theme.EcoGreen
import com.ecotrack.core.design.theme.ExpiryCritical

@Composable
fun UsageDonutChart(
    used: Int,
    wasted: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val total = (used + wasted).coerceAtLeast(1)
    val usedSweep = 360f * used / total
    val wastedSweep = 360f * wasted / total

    Canvas(
        modifier = modifier.then(
            if (contentDescription != null) {
                Modifier.semantics { this.contentDescription = contentDescription }
            } else {
                Modifier
            },
        ),
    ) {
        val stroke = Stroke(width = 24f, cap = StrokeCap.Round)
        val diameter = size.minDimension - stroke.width
        val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
        val arcSize = Size(diameter, diameter)

        drawArc(
            color = EcoGreen,
            startAngle = -90f,
            sweepAngle = usedSweep,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = stroke,
        )
        drawArc(
            color = ExpiryCritical,
            startAngle = -90f + usedSweep,
            sweepAngle = wastedSweep,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = stroke,
        )
    }
}
