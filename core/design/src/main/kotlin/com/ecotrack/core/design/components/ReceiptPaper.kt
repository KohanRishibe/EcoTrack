package com.ecotrack.core.design.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecotrack.core.design.theme.EcoReceiptFontFamily
import com.ecotrack.core.design.theme.ReceiptDivider
import com.ecotrack.core.design.theme.ReceiptInk
import com.ecotrack.core.design.theme.ReceiptInkMuted
import com.ecotrack.core.design.theme.ReceiptPaper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ReceiptPaper(
    modifier: Modifier = Modifier,
    storeName: String = "ECOTRACK",
    storeSubtitle: String = "СПИСОК ПОКУПОК",
    footerLine: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm"))

    Column(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
            .background(ReceiptPaper),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = storeName,
                fontFamily = EcoReceiptFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 2.sp,
                color = ReceiptInk,
                textAlign = TextAlign.Center,
            )
            Text(
                text = storeSubtitle,
                fontFamily = EcoReceiptFontFamily,
                fontSize = 11.sp,
                letterSpacing = 1.2.sp,
                color = ReceiptInkMuted,
                textAlign = TextAlign.Center,
            )
            Text(
                text = timestamp,
                fontFamily = EcoReceiptFontFamily,
                fontSize = 10.sp,
                color = ReceiptInkMuted,
            )
            ReceiptDashedDivider(color = ReceiptDivider, modifier = Modifier.padding(vertical = 8.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 12.dp),
            content = content,
        )

        footerLine?.let { line ->
            ReceiptDashedDivider(
                color = ReceiptDivider,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
            Text(
                text = line,
                fontFamily = EcoReceiptFontFamily,
                fontSize = 10.sp,
                color = ReceiptInkMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp),
            )
        }

        ReceiptTearEdge(
            paperColor = ReceiptPaper,
            edgeColor = MaterialTheme.colorScheme.background,
        )
    }
}

@Composable
fun ReceiptDashedDivider(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        val dashWidth = 6f
        val gap = 4f
        var x = 0f
        while (x < size.width) {
            drawLine(
                color = color,
                start = Offset(x, size.height / 2f),
                end = Offset((x + dashWidth).coerceAtMost(size.width), size.height / 2f),
                strokeWidth = 1.5f,
            )
            x += dashWidth + gap
        }
    }
}

@Composable
fun ReceiptLineItem(
    name: String,
    detail: String? = null,
    trailing: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = name,
                fontFamily = EcoReceiptFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = ReceiptInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            ReceiptDotLeader(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                color = ReceiptInkMuted.copy(alpha = 0.5f),
            )
            Text(
                text = trailing,
                fontFamily = EcoReceiptFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ReceiptInk,
            )
        }
        detail?.let {
            Text(
                text = it,
                fontFamily = EcoReceiptFontFamily,
                fontSize = 10.sp,
                color = ReceiptInkMuted,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun ReceiptDotLeader(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.padding(bottom = 3.dp)) {
        val dotSpacing = 5f
        val y = size.height - 2f
        var x = 0f
        while (x < size.width) {
            drawCircle(color = color, radius = 1f, center = Offset(x, y))
            x += dotSpacing
        }
    }
}

@Composable
private fun ReceiptTearEdge(
    paperColor: Color,
    edgeColor: Color,
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
    ) {
        val toothWidth = 14f
        val toothHeight = 10f
        val path = Path().apply {
            moveTo(0f, 0f)
            var x = 0f
            var pointUp = true
            while (x <= size.width) {
                val y = if (pointUp) 0f else toothHeight
                lineTo(x, y)
                x += toothWidth / 2f
                pointUp = !pointUp
            }
            lineTo(size.width, toothHeight)
            lineTo(size.width, toothHeight + 4f)
            lineTo(0f, toothHeight + 4f)
            close()
        }
        drawPath(path, paperColor)
        drawPath(path, edgeColor.copy(alpha = 0.08f))
    }
}

@Composable
fun ReceiptTotalRow(
    label: String,
    value: String,
    emphasized: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(
            color = ReceiptDivider,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                fontFamily = EcoReceiptFontFamily,
                fontSize = if (emphasized) 14.sp else 12.sp,
                fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Normal,
                color = ReceiptInk,
            )
            Text(
                text = value,
                fontFamily = EcoReceiptFontFamily,
                fontSize = if (emphasized) 14.sp else 12.sp,
                fontWeight = FontWeight.Bold,
                color = ReceiptInk,
            )
        }
    }
}
