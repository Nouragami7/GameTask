package com.example.game.data.repository

import com.example.game.data.local.dao.GamesDao
import com.example.game.data.mapper.toDomain
import com.example.game.data.mapper.toDomainDetails
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
            val genres = response.results.map { it.toDomain() }
            dao.insertGenres(genres.map { it.toEntity() })
            Result.success(genres)
        } catch (e: Exception) {
            val cached = dao.getGenres()
            if (cached.isNotEmpty()) {
                Result.success(cached.map { it.toDomain() })
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getGamesByGenre(genre: String, page: Int): Result<List<Game>> {
        return try {
            val response = api.getGames(genres = genre, page = page)
            val games = response.results.map { it.toDomain() }
            if (page == 1) dao.clearGenre(genre)
            dao.insertGames(games.map { it.toEntity(genre = genre, page = page) })
            Result.success(games)
        } catch (e: Exception) {
            val cached = dao.getGamesByGenre(genre)
            if (cached.isNotEmpty()) {
                Result.success(cached.map { it.toDomain() })
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getGameDetails(gameId: Int): Result<GameDetails> {
        return try {
            val details = api.getGameDetails(gameId).toDomain()

            val screenshots = try {
                api.getGameScreenshots(gameId).results.map { it.image }
            } catch (e: Exception) {
                emptyList()
            }

            val trailerUrl = try {
                api.getGameTrailers(gameId).results.firstOrNull()?.data?.max
            } catch (e: Exception) {
                null
            }

            val fullDetails = details.copy(screenshots = screenshots, trailerUrl = trailerUrl)

            dao.insertGameDetails(fullDetails.toEntity())

            Result.success(fullDetails)
        } catch (e: Exception) {
            val cached = dao.getGameDetails(gameId)
            if (cached != null) {
                Result.success(cached.toDomainDetails())
            } else {
                Result.failure(e)
            }
        }
    }
}