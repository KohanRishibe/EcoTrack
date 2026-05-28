package com.ecotrack.feature.dashboard.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ecotrack.core.design.components.EcoElevatedCard
import com.ecotrack.core.design.theme.EcoAccentAmber
import com.ecotrack.core.design.theme.EcoNeutralAccent
import com.ecotrack.core.design.theme.ExpiryCritical

@Composable
fun DashboardStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accentColor: androidx.compose.ui.graphics.Color = EcoNeutralAccent,
) {
    EcoElevatedCard(
        modifier = modifier,
        accentColor = accentColor,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
fun DashboardStatsGrid(
    totalProducts: Int,
    totalUnits: Int,
    expiringCount: Int,
    expiredCount: Int,
    cartCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        ) {
            DashboardStatCard(
                label = "Позиций",
                value = totalProducts.toString(),
                modifier = Modifier.weight(1f),
            )
            DashboardStatCard(
                label = "Штук",
                value = totalUnits.toString(),
                modifier = Modifier.weight(1f),
            )
        }
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        ) {
            DashboardStatCard(
                label = "Истекает",
                value = expiringCount.toString(),
                modifier = Modifier.weight(1f),
                accentColor = EcoAccentAmber,
            )
            DashboardStatCard(
                label = "Просрочено",
                value = expiredCount.toString(),
                modifier = Modifier.weight(1f),
                accentColor = ExpiryCritical,
            )
        }
        if (cartCount > 0) {
            DashboardStatCard(
                label = "В корзине",
                value = cartCount.toString(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
