package com.farmerinven.apsola.ui.navigation

sealed class Screen(val route: String) {
    data object Inventory : Screen("inventory")
    data object AddEditItem : Screen("add_edit_item/{itemId}") {
        fun createRoute(itemId: Long? = null): String {
            return if (itemId != null) "add_edit_item/$itemId" else "add_edit_item/0"
        }
    }
    data object ItemDetail : Screen("item_detail/{itemId}") {
        fun createRoute(itemId: Long): String = "item_detail/$itemId"
    }
    data object Categories : Screen("categories")
    data object Analytics : Screen("analytics")
    data object Settings : Screen("settings")
}
