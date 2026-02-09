package com.example.game.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_details")
data class GameDetailsEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val rating: Double,
    val releaseDate: String,
    val description: String,
    val metacritic: Int?,
    val genres: String,
    val screenshots: String,
    val trailerUrl: String?
)