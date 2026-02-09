package com.example.game.data.mapper

import com.example.game.data.local.entity.GameDetailsEntity
import com.example.game.data.local.entity.GameEntity
import com.example.game.data.local.entity.GenreEntity
import com.example.game.data.remote.dto.GameDetailsDto
import com.example.game.data.remote.dto.GameDto
import com.example.game.data.remote.dto.GenreDto
import com.example.game.domain.model.Game
import com.example.game.domain.model.GameDetails
import com.example.game.domain.model.Genre

fun GenreDto.toDomain() = Genre(
    id = id,
    name = name,
    slug = slug,
    imageUrl = imageBackground ?: ""
)

fun GameDto.toDomain() = Game(
    id = id,
    name = name,
    imageUrl = backgroundImage ?: "",
    rating = rating,
    releaseDate = released ?: "Unknown",
    genres = genres?.map { it.name } ?: emptyList()
)

fun GameDetailsDto.toDomain(
    screenshots: List<String> = emptyList(),
    trailerUrl: String? = null
) = GameDetails(
    id = id,
    name = name,
    imageUrl = backgroundImage ?: "",
    rating = rating,
    releaseDate = released ?: "Unknown",
    description = descriptionRaw ?: "No description available.",
    metacritic = metacritic,
    genres = genres?.map { it.name } ?: emptyList(),
    screenshots = screenshots,
    trailerUrl = trailerUrl
)

fun Game.toEntity(genre: String, page: Int) = GameEntity(
    id = id,
    name = name,
    imageUrl = imageUrl,
    rating = rating,
    releaseDate = releaseDate,
    genres = genres.joinToString(","),
    genreFilter = genre,
    page = page,
    cachedAt = System.currentTimeMillis()
)

fun GameEntity.toDomain() = Game(
    id = id,
    name = name,
    imageUrl = imageUrl,
    rating = rating,
    releaseDate = releaseDate,
    genres = genres.split(",").filter { it.isNotBlank() }
)

fun Genre.toEntity(): GenreEntity = GenreEntity(
    id = id,
    name = name,
    slug = slug,
    imageUrl = imageUrl
)

fun GenreEntity.toDomain(): Genre = Genre(
    id = id,
    name = name,
    slug = slug,
    imageUrl = imageUrl
)

fun GameDetails.toEntity(): GameDetailsEntity = GameDetailsEntity(
    id = id,
    name = name,
    imageUrl = imageUrl,
    rating = rating,
    releaseDate = releaseDate,
    description = description,
    metacritic = metacritic,
    genres = genres.joinToString(","),
    screenshots = screenshots.joinToString(","),
    trailerUrl = trailerUrl
)

fun GameDetailsEntity.toDomainDetails(): GameDetails = GameDetails(
    id = id,
    name = name,
    imageUrl = imageUrl,
    rating = rating,
    releaseDate = releaseDate,
    description = description,
    metacritic = metacritic,
    genres = genres.split(",").filter { it.isNotBlank() },
    screenshots = screenshots.split(",").filter { it.isNotBlank() },
    trailerUrl = trailerUrl
)