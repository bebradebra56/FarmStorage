package com.farmerinven.apsola.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.farmerinven.apsola.ui.screens.*
import com.farmerinven.apsola.ui.viewmodel.*

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModelFactory: ViewModelFactory,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Inventory.route,
        modifier = modifier
    ) {
        composable(Screen.Inventory.route) {
            val viewModel: InventoryViewModel = viewModel(factory = viewModelFactory)
            InventoryScreen(
                viewModel = viewModel,
                onNavigateToAddItem = { navController.navigate(Screen.AddEditItem.createRoute()) },
                onNavigateToItemDetail = { itemId ->
                    navController.navigate(Screen.ItemDetail.createRoute(itemId))
                },
                onNavigateToCategories = { navController.navigate(Screen.Categories.route) }
            )
        }

        composable(
            route = Screen.AddEditItem.route,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId") ?: 0
            val viewModel: AddEditItemViewModel = viewModel(factory = viewModelFactory)
            AddEditItemScreen(
                viewModel = viewModel,
                itemId = if (itemId > 0) itemId else null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ItemDetail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId") ?: 0
            val viewModel: ItemDetailViewModel = viewModel(factory = viewModelFactory)
            ItemDetailScreen(
                viewModel = viewModel,
                itemId = itemId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { 
                    navController.navigate(Screen.AddEditItem.createRoute(itemId))
                }
            )
        }

        composable(Screen.Categories.route) {
            val viewModel: CategoryViewModel = viewModel(factory = viewModelFactory)
            CategoriesScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Analytics.route) {
            val viewModel: AnalyticsViewModel = viewModel(factory = viewModelFactory)
            AnalyticsScreen(
                viewModel = viewModel
            )
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
            SettingsScreen(
                viewModel = viewModel
            )
        }
    }
}
