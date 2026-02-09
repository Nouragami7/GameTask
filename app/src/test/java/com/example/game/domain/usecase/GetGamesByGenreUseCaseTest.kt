package com.example.game.domain.usecase

import com.example.game.domain.model.Game
import com.example.game.domain.repository.GamesRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class GetGamesByGenreUseCaseTest {

    @MockK
    private lateinit var repository: GamesRepository

    private lateinit var useCase: GetGamesByGenreUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        useCase = GetGamesByGenreUseCase(repository)
    }

    @Test
    fun invoke_returnsGamesOnSuccess() = runTest {
        // Given
        val game1: Game = mockk(relaxed = true) {
            every { name } returns "GTA V"
            every { rating } returns 4.5
        }
        val game2: Game = mockk(relaxed = true) {
            every { name } returns "Witcher 3"
            every { rating } returns 4.7
        }
        coEvery { repository.getGamesByGenre("action", 1) } returns Result.success(listOf(game1, game2))

        // When
        val result = useCase("action", 1)

        // Then
        assertThat(result.isSuccess, `is`(true))
        assertThat(result.getOrNull()?.size, `is`(2))
        assertThat(result.getOrNull()?.first()?.name, `is`("GTA V"))
        assertThat(result.getOrNull()?.first()?.rating, `is`(4.5))
        coVerify(exactly = 1) { repository.getGamesByGenre("action", 1) }
    }

    @Test
    fun invoke_returnsEmptyListWhenNoGames() = runTest {
        // Given
        coEvery { repository.getGamesByGenre("action", 1) } returns Result.success(emptyList())

        // When
        val result = useCase("action", 1)

        // Then
        assertThat(result.isSuccess, `is`(true))
        assertThat(result.getOrNull()?.isEmpty(), `is`(true))
    }

    @Test
    fun invoke_returnsFailureOnError() = runTest {
        // Given
        coEvery { repository.getGamesByGenre("action", 1) } returns Result.failure(Exception("Server error"))

        // When
        val result = useCase("action", 1)

        // Then
        assertThat(result.isFailure, `is`(true))
        assertThat(result.exceptionOrNull()?.message, `is`("Server error"))
    }

    @Test
    fun invoke_passesCorrectParams() = runTest {
        // Given
        val game: Game = mockk(relaxed = true)
        coEvery { repository.getGamesByGenre(any(), any()) } returns Result.success(listOf(game))

        // When
        useCase("rpg", 3)

        // Then
        coVerify(exactly = 1) { repository.getGamesByGenre("rpg", 3) }
    }
}