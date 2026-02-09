package com.example.game.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreensRoute {

    @Serializable
    data object SplashScreen : ScreensRoute()
    @Serializable
    data object GamesListScreen : ScreensRoute()

    @Serializable
    data class GameDetailsScreen(val gameId: Int) : ScreensRoute()
}