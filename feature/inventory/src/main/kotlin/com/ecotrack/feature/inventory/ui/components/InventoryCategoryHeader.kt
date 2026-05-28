package com.ecotrack.feature.inventory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ecotrack.core.design.theme.EcoGreen
import com.ecotrack.domain.model.ProductCategory

@Composable
fun InventoryCategoryHeader(
    category: ProductCategory,
    itemCount: Int,
    modifier: Modifier = Modifier,
) {
    val accent = categoryAccentColor(category)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(accent),
        )
        Text(
            text = category.displayName,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        )
        Surface(
            shape = MaterialTheme.shapes.small,
            color = accent.copy(alpha = 0.15f),
        ) {
            Text(
                text = itemCount.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = accent,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
    }
}

private fun categoryAccentColor(category: ProductCategory): Color = when (category) {
    ProductCategory.DAIRY -> Color(0xFF5C6BC0)
    ProductCategory.MEAT -> Color(0xFFE57373)
    ProductCategory.VEGETABLES -> EcoGreen
    ProductCategory.FRUITS -> Color(0xFFFFB74D)
    ProductCategory.BAKERY -> Color(0xFF8D6E63)
    ProductCategory.BEVERAGES -> Color(0xFF4DD0E1)
    ProductCategory.FROZEN -> Color(0xFF81D4FA)
    ProductCategory.OTHER -> Color(0xFF90A4AE)
}
