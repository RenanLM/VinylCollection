package br.com.renan.vinylcollection.ui.navigation

sealed class Screen(val route: String) {
    object Settings : Screen("settings_screen")
    object Home : Screen("home_screen")
    object Search : Screen("search_screen")
    object Detail : Screen("detail_screen/{vinylId}") {
        fun createRoute(vinylId: Int) = "detail_screen/$vinylId"
    }
}