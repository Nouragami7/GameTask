package com.example.game.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val imageUrl: String,
    val rating: Double,
    val releaseDate: String,
    val genres: String,
    val genreFilter: String,
    val page: Int,
    val cachedAt: Long
)