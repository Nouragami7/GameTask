package com.example.game.data.remote.dto

data class ScreenshotsResponseDto(
    val count: Int,
    val results: List<ScreenshotDto>
)

data class ScreenshotDto(
    val id: Int,
    val image: String
)