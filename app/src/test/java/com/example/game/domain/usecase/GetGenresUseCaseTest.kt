package com.example.game.domain.usecase

import com.example.game.domain.model.Genre
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

class GetGenresUseCaseTest {

    @MockK
    private lateinit var repository: GamesRepository

    private lateinit var useCase: GetGenresUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        useCase = GetGenresUseCase(repository)
    }

    @Test
    fun invoke_returnsGenresOnSuccess() = runTest {
        // Given
        val genre1: Genre = mockk(relaxed = true) {
            every { name } returns "Action"
        }
        val genre2: Genre = mockk(relaxed = true) {
            every { name } returns "RPG"
        }
        coEvery { repository.getGenres() } returns Result.success(listOf(genre1, genre2))

        // When
        val result = useCase()

        // Then
        assertThat(result.isSuccess, `is`(true))
        assertThat(result.getOrNull()?.size, `is`(2))
        assertThat(result.getOrNull()?.first()?.name, `is`("Action"))
        coVerify(exactly = 1) { repository.getGenres() }
    }

    @Test
    fun invoke_returnsFailureOnError() = runTest {
        // Given
        coEvery { repository.getGenres() } returns Result.failure(Exception("Network error"))

        // When
        val result = useCase()

        // Then
        assertThat(result.isFailure, `is`(true))
        assertThat(result.exceptionOrNull()?.message, `is`("Network error"))
    }

    @Test
    fun invoke_returnsEmptyList() = runTest {
        // Given
        coEvery { repository.getGenres() } returns Result.success(emptyList())

        // When
        val result = useCase()

        // Then
        assertThat(result.isSuccess, `is`(true))
        assertThat(result.getOrNull()?.isEmpty(), `is`(true))
    }
}