package com.example.game.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TrailersResponseDto(
    val count: Int,
    val results: List<TrailerDto>
)

data class TrailerDto(
    val id: Int,
    val name: String,
    val data: TrailerDataDto?
)

data class TrailerDataDto(
    val max: String?,
    @SerializedName("480")
    val lowQuality: String?
)