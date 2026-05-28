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
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ecotrack.core.design.components.EcoElevatedCard
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
    val accent = expiryColor(status)

    EcoElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .ecoTouchTarget()
            .clickable(onClick = onClick)
            .semantics { contentDescription = "${item.name}, $expiryDescription" },
        accentColor = accent,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
            ) {
                if (item.imageUrl != null) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = "Изображение ${item.name}",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(MaterialTheme.shapes.small),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Иконка продукта",
                        modifier = Modifier
                            .size(52.dp)
                            .padding(12.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "До ${item.expiryDateLabel} · ${item.quantityLabel}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = item.categoryLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(accent)
                        .semantics { contentDescription = expiryDescription },
                )
                Text(
                    text = when (status) {
                        ExpiryStatus.FRESH -> "Ок"
                        ExpiryStatus.WARNING -> "Скоро"
                        ExpiryStatus.CRITICAL -> "!"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
