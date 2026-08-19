package br.com.renan.vinylcollection.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.renan.vinylcollection.ui.screens.DetailScreen
import br.com.renan.vinylcollection.ui.screens.HomeScreen
import br.com.renan.vinylcollection.ui.screens.SearchScreen
import br.com.renan.vinylcollection.ui.viewmodel.VinylViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val sharedViewModel: VinylViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = sharedViewModel,
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToDetail = { vinylId -> navController.navigate(Screen.Detail.createRoute(vinylId)) }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                viewModel = sharedViewModel,
                onBackClick = { navController.popBackStack() },
                onVinylClick = { vinylId -> navController.navigate(Screen.Detail.createRoute(vinylId)) }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("vinylId") { type = NavType.IntType })
        ) { backStackEntry ->
            val vinylId = backStackEntry.arguments?.getInt("vinylId") ?: 0

            DetailScreen(
                vinylId = vinylId,
                viewModel = sharedViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}