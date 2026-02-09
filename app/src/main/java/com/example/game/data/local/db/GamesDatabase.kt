package com.example.game.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.game.data.local.dao.GamesDao
import com.example.game.data.local.entity.GameDetailsEntity
import com.example.game.data.local.entity.GameEntity
import com.example.game.data.local.entity.GenreEntity

@Database(
    entities = [GameEntity::class, GenreEntity::class, GameDetailsEntity::class],
    version = 4,
    exportSchema = false
)
abstract class GamesDatabase : RoomDatabase() {
    abstract fun gamesDao(): GamesDao
}