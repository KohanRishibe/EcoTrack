package com.ecotrack.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ecotrack.feature.addproduct.ui.AddProductScreen
import com.ecotrack.feature.addproduct.ui.scan.BarcodeScanScreen
import com.ecotrack.feature.ai.ui.photo.PhotoRecognizeScreen
import com.ecotrack.feature.ai.ui.receipt.ReceiptScanScreen
import com.ecotrack.feature.dashboard.ui.DashboardScreen
import com.ecotrack.feature.inventory.ui.InventoryScreen
import com.ecotrack.feature.productdetail.ui.ProductDetailScreen
import com.ecotrack.feature.settings.ui.SettingsScreen
import com.ecotrack.feature.shoppinglist.ui.ShoppingListScreen

private data class TopLevelDestination(
    val route: Any,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun EcoTrackNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val topLevelDestinations = listOf(
        TopLevelDestination(DashboardRoute, "Главная", Icons.Default.Home),
        TopLevelDestination(InventoryRoute, "Запасы", Icons.Default.Inventory2),
        TopLevelDestination(ShoppingListRoute, "Покупки", Icons.Default.ShoppingCart),
    )

    val showBottomBar = topLevelDestinations.any { dest ->
        currentDestination?.hierarchy?.any { it.hasRoute(dest.route::class) } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.hasRoute(destination.route::class)
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(DashboardRoute) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentDestination?.hasRoute(InventoryRoute::class) == true) {
                FloatingActionButton(
                    onClick = { navController.navigate(BarcodeScanRoute) },
                    shape = androidx.compose.foundation.shape.CircleShape,
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Сканировать штрихкод")
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = DashboardRoute,
            modifier = Modifier.padding(padding),
            enterTransition = {
                fadeIn(tween(300)) + slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(300),
                )
            },
            exitTransition = {
                fadeOut(tween(300)) + slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(300),
                )
            },
        ) {
            composable<DashboardRoute> {
                DashboardScreen(
                    onOpenSettings = { navController.navigate(SettingsRoute) },
                    onOpenPhotoRecognize = { navController.navigate(PhotoRecognizeRoute) },
                    onOpenReceiptScan = { navController.navigate(ReceiptScanRoute) },
                )
            }
            composable<InventoryRoute> {
                InventoryScreen(
                    onProductClick = { id ->
                        navController.navigate(ProductDetailRoute(id))
                    },
                    onAddProduct = { navController.navigate(AddProductRoute()) },
                )
            }
            composable<ShoppingListRoute> {
                ShoppingListScreen()
            }
            composable<BarcodeScanRoute> {
                BarcodeScanScreen(
                    onBack = { navController.popBackStack() },
                    onOpenAddProduct = { barcode ->
                        navController.navigate(AddProductRoute(barcode = barcode)) {
                            popUpTo(BarcodeScanRoute) { inclusive = true }
                        }
                    },
                    onOpenProductDetail = { productId ->
                        navController.navigate(ProductDetailRoute(productId)) {
                            popUpTo(BarcodeScanRoute) { inclusive = true }
                        }
                    },
                    onManualEntry = {
                        navController.navigate(AddProductRoute()) {
                            popUpTo(BarcodeScanRoute) { inclusive = true }
                        }
                    },
                )
            }
            composable<AddProductRoute> {
                AddProductScreen(
                    onSaved = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                    onRecognizeByPhoto = { navController.navigate(PhotoRecognizeRoute) },
                )
            }
            composable<ProductDetailRoute> {
                ProductDetailScreen(
                    onBack = { navController.popBackStack() },
                )
            }
            composable<SettingsRoute> {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onOpenPhotoRecognize = { navController.navigate(PhotoRecognizeRoute) },
                    onOpenReceiptScan = { navController.navigate(ReceiptScanRoute) },
                )
            }
            composable<PhotoRecognizeRoute> {
                PhotoRecognizeScreen(
                    onBack = { navController.popBackStack() },
                    onApplyToAddProduct = { insight ->
                        navController.navigate(
                            AddProductRoute(
                                suggestedName = insight.suggestedName,
                                suggestedCategory = insight.category.name,
                                suggestedExpiryDate = insight.suggestedExpiryDate.toString(),
                            ),
                        ) {
                            popUpTo(PhotoRecognizeRoute) { inclusive = true }
                        }
                    },
                )
            }
            composable<ReceiptScanRoute> {
                ReceiptScanScreen(
                    onBack = { navController.popBackStack() },
                    onImportComplete = { navController.popBackStack() },
                )
            }
        }
    }
}
