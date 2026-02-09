package com.example.game.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.game.presentation.gamedetails.screen.GameDetailsScreen
import com.example.game.presentation.gameslist.screen.GamesListScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = ScreensRoute.GamesListScreen
    ) {
        composable<ScreensRoute.GamesListScreen> {
            GamesListScreen(
                onGameClick = { gameId ->
                    navController.navigate(ScreensRoute.GameDetailsScreen(gameId))
                }
            )
        }

        composable<ScreensRoute.GameDetailsScreen> {
            GameDetailsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}