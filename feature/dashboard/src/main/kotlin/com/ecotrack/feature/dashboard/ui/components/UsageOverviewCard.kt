package com.ecotrack.feature.dashboard.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import com.ecotrack.core.design.components.EcoElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ecotrack.core.design.theme.ExpiryCritical

@Composable
fun UsageOverviewCard(
    used: Int,
    wasted: Int,
    utilizationPercent: Int,
    modifier: Modifier = Modifier,
) {
    val total = used + wasted

    EcoElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Расход продуктов", style = MaterialTheme.typography.titleMedium)
            Text(
                "Журнал за всё время — не сбрасывается при удалении из запасов",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    UsageDonutChart(
                        used = used,
                        wasted = wasted,
                        modifier = Modifier.size(140.dp),
                        contentDescription = "Использовано $used, выброшено $wasted",
                    )
                    if (total > 0) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$utilizationPercent%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "съедено",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    UsageLegendRow(
                        color = MaterialTheme.colorScheme.primary,
                        label = "Использовано",
                        value = used.toString(),
                    )
                    UsageLegendRow(
                        color = ExpiryCritical,
                        label = "Выброшено",
                        value = wasted.toString(),
                    )
                    Text(
                        text = if (total == 0) {
                            "Отмечайте «Использован» или «Выброшен» в карточке продукта"
                        } else {
                            "Всего списаний: $total"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun UsageLegendRow(
    color: androidx.compose.ui.graphics.Color,
    label: String,
    value: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(12.dp)) {
            drawCircle(color = color)
        }
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
