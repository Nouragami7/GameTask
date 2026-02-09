package com.example.game.domain.model

data class GameDetails(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val rating: Double,
    val releaseDate: String,
    val description: String,
    val metacritic: Int?,
    val genres: List<String> = emptyList(),
    val screenshots: List<String> = emptyList(),
    val trailerUrl: String? = null
)