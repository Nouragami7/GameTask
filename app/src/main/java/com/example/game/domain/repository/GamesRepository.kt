package com.example.game.domain.repository

import com.example.game.domain.model.Game
import com.example.game.domain.model.GameDetails
import com.example.game.domain.model.Genre

interface GamesRepository {
    suspend fun getGenres(): Result<List<Genre>>
    suspend fun getGamesByGenre(genre: String, page: Int): Result<List<Game>>
    suspend fun getGameDetails(gameId: Int): Result<GameDetails>


}