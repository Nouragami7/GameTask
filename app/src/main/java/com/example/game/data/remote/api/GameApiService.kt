package com.example.game.data.remote.api

import com.example.game.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GameApiService {

    @GET("genres")
    suspend fun getGenres(): GenresResponseDto

    @GET("games")
    suspend fun getGames(
        @Query("genres") genres: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 20
    ): GamesResponseDto

    @GET("games/{id}")
    suspend fun getGameDetails(
        @Path("id") gameId: Int
    ): GameDetailsDto

    @GET("games/{id}/screenshots")
    suspend fun getGameScreenshots(
        @Path("id") gameId: Int
    ): ScreenshotsResponseDto

    @GET("games/{id}/movies")
    suspend fun getGameTrailers(
        @Path("id") gameId: Int
    ): TrailersResponseDto
}