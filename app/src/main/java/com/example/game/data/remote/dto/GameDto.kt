package com.example.game.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GamesResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<GameDto>
)

data class GameDto(
    val id: Int,
    val name: String,
    @SerializedName("background_image")
    val backgroundImage: String?,
    val rating: Double,
    val released: String?,
    val genres: List<GameGenreDto>?
)

data class GameGenreDto(
    val id: Int,
    val name: String
)