package com.example.game.data.repository

import com.example.game.data.local.dao.GamesDao
import com.example.game.data.mapper.toDomain
import com.example.game.data.mapper.toEntity
import com.example.game.data.remote.api.GameApiService
import com.example.game.domain.model.Game
import com.example.game.domain.model.GameDetails
import com.example.game.domain.model.Genre
import com.example.game.domain.repository.GamesRepository
import javax.inject.Inject

class GamesRepositoryImpl @Inject constructor(
    private val api: GameApiService,
    private val dao: GamesDao
) : GamesRepository {

    override suspend fun getGenres(): Result<List<Genre>> {
        return try {
            val response = api.getGenres()
            Result.success(response.results.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGames(genre: String, page: Int): Result<List<Game>> {
        return try {
            val response = api.getGames(genre, page)
            val games = response.results.map { it.toDomain() }

            try {
                if (page == 1) dao.clearGenre(genre)
                dao.insertGames(games.map { it.toEntity(genre, page) })
            } catch (_: Exception) { }

            Result.success(games)
        } catch (e: Exception) {
            try {
                val cached = dao.getGamesByGenre(genre)
                if (cached.isNotEmpty()) {
                    Result.success(cached.map { it.toDomain() })
                } else {
                    Result.failure(e)
                }
            } catch (_: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getGameDetails(gameId: Int): Result<GameDetails> {
        return try {
            val details = api.getGameDetails(gameId)

            val screenshots = try {
                api.getGameScreenshots(gameId).results.map { it.image }
            } catch (_: Exception) { emptyList() }

            val trailerUrl = try {
                api.getGameTrailers(gameId).results.firstOrNull()?.data?.max
            } catch (_: Exception) { null }

            Result.success(details.toDomain(screenshots, trailerUrl))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGameScreenshots(gameId: Int): Result<List<String>> {
        return try {
            val response = api.getGameScreenshots(gameId)
            Result.success(response.results.map { it.image })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGameTrailers(gameId: Int): Result<String?> {
        return try {
            val response = api.getGameTrailers(gameId)
            Result.success(response.results.firstOrNull()?.data?.max)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}