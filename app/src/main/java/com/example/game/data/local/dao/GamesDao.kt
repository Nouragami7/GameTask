package com.example.game.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.game.data.local.entity.GameDetailsEntity
import com.example.game.data.local.entity.GameEntity
import com.example.game.data.local.entity.GenreEntity

@Dao
interface GamesDao {

    @Query("SELECT DISTINCT * FROM games WHERE genreFilter = :genre ORDER BY page ASC")
    suspend fun getGamesByGenre(genre: String): List<GameEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)

    @Query("DELETE FROM games WHERE genreFilter = :genre")
    suspend fun clearGenre(genre: String)

    @Query("DELETE FROM games")
    suspend fun clearAll()


    @Query("SELECT * FROM genres")
    suspend fun getGenres(): List<GenreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Query("SELECT * FROM game_details WHERE id = :gameId")
    suspend fun getGameDetails(gameId: Int): GameDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameDetails(details: GameDetailsEntity)
}
