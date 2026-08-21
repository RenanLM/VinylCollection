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
import br.com.renan.vinylcollection.ui.screens.SettingsScreen
import br.com.renan.vinylcollection.ui.viewmodel.SettingsViewModel
import br.com.renan.vinylcollection.ui.viewmodel.VinylViewModel

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val sharedViewModel: VinylViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        // Animação ao ENTRAR em uma nova tela
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            ) + fadeIn(animationSpec = tween(400))
        },
        // Animação da tela anterior SAINDO
        exitTransition = {
            fadeOut(animationSpec = tween(400))
        },
        // Animação ao VOLTAR
        popEnterTransition = {
            fadeIn(animationSpec = tween(400))
        },
        // Animação da tela atual SAINDO ao voltar
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            ) + fadeOut(animationSpec = tween(400))
        }
    ) {

        composable(Screen.Settings.route) {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            SettingsScreen(
                viewModel = settingsViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = sharedViewModel,
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToDetail = { vinylId -> navController.navigate(Screen.Detail.createRoute(vinylId)) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
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