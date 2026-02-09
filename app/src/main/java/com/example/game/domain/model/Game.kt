package com.example.game.domain.model

data class Game(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val rating: Double,
    val releaseDate: String,
    val genres: List<String> = emptyList()
)