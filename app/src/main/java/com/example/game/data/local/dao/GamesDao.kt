package com.example.game.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.game.data.local.entity.GameEntity

@Dao
interface GamesDao {

    @Query("SELECT * FROM games WHERE genreFilter = :genre ORDER BY page ASC")
    suspend fun getGamesByGenre(genre: String): List<GameEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)

    @Query("DELETE FROM games WHERE genreFilter = :genre")
    suspend fun clearGenre(genre: String)

    @Query("DELETE FROM games")
    suspend fun clearAll()
}