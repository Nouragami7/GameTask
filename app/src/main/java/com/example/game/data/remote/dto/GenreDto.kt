package com.example.game.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GenresResponseDto(
    val count: Int,
    val results: List<GenreDto>
)

data class GenreDto(
    val id: Int,
    val name: String,
    val slug: String,
    @SerializedName("image_background")
    val imageBackground: String?
)