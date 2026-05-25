package com.ecotrack.feature.inventory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ecotrack.core.ui.util.ExpiryStatus
import com.ecotrack.core.ui.util.ecoTouchTarget
import com.ecotrack.core.ui.util.expiryColor
import com.ecotrack.core.ui.util.expiryStatus
import com.ecotrack.feature.inventory.ui.InventoryItemUi

@Composable
fun InventoryProductRow(
    item: InventoryItemUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val status = expiryStatus(item.expiryDate)
    val expiryDescription = when (status) {
        ExpiryStatus.FRESH -> "Срок годности в норме"
        ExpiryStatus.WARNING -> "Срок годности скоро истекает"
        ExpiryStatus.CRITICAL -> "Срок годности критичен"
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .ecoTouchTarget()
            .clickable(onClick = onClick)
            .semantics { contentDescription = "${item.name}, $expiryDescription" },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (item.imageUrl != null) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = "Изображение ${item.name}",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialTheme.shapes.small),
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = "Иконка продукта",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "До ${item.expiryDateLabel} · ${item.quantityLabel}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(expiryColor(status))
                    .semantics { contentDescription = expiryDescription },
            )
        }
    }
}
