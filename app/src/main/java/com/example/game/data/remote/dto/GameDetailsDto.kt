package com.example.game.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GameDetailsDto(
    val id: Int,
    val name: String,
    @SerializedName("background_image")
    val backgroundImage: String?,
    val rating: Double,
    val released: String?,
    @SerializedName("description_raw")
    val descriptionRaw: String?,
    val metacritic: Int?,
    val genres: List<GameGenreDto>?
)