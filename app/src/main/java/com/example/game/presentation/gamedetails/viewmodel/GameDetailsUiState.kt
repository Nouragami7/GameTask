package com.example.game.presentation.gamedetails.viewmodel

import com.example.game.domain.model.GameDetails

data class GameDetailsUiState(
    val gameDetails: GameDetails? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)