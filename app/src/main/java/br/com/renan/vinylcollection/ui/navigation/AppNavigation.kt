package br.com.renan.vinylcollection.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.renan.vinylcollection.ui.screens.DetailScreen
import br.com.renan.vinylcollection.ui.screens.HomeScreen
import br.com.renan.vinylcollection.ui.screens.SearchScreen
import br.com.renan.vinylcollection.ui.viewmodel.VinylViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {

            val viewModel = hiltViewModel<VinylViewModel>()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToDetail = { vinylId -> navController.navigate(Screen.Detail.createRoute(vinylId)) }
            )
        }

        composable(Screen.Search.route) {
            val viewModel = hiltViewModel<VinylViewModel>()
            SearchScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onVinylClick = { vinylId -> navController.navigate(Screen.Detail.createRoute(vinylId)) }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("vinylId") { type = NavType.IntType })
        ) { backStackEntry ->
            val viewModel = hiltViewModel<VinylViewModel>()
            val vinylId = backStackEntry.arguments?.getInt("vinylId") ?: 0

            DetailScreen(
                vinylId = vinylId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}