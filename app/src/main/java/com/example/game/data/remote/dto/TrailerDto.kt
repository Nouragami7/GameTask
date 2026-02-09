package com.example.game.data.remote.dto

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
    val `480`: String?
)