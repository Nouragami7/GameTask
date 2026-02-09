package com.example.game.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.game.data.local.dao.GamesDao
import com.example.game.data.local.entity.GameEntity

@Database(
    entities = [GameEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GamesDatabase : RoomDatabase() {
    abstract fun gamesDao(): GamesDao
}