package com.ecotrack.feature.inventory.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ecotrack.core.design.components.EcoElevatedCard
import com.ecotrack.feature.inventory.domain.InventoryGrouped

@Composable
fun InventoryScreenHeader(
    grouped: InventoryGrouped,
    onAddProduct: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalItems = grouped.groups.values.sumOf { it.size }
    val categoryCount = grouped.groups.size

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Запасы",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = "Всё, что есть дома — по категориям",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            FilledTonalButton(onClick = onAddProduct) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Добавить", modifier = Modifier.padding(start = 4.dp))
            }
        }

        EcoElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                InventoryStatChip(label = "Позиций", value = totalItems.toString())
                InventoryStatChip(label = "Категорий", value = categoryCount.toString())
            }
        }
    }
}

@Composable
private fun InventoryStatChip(
    label: String,
    value: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
