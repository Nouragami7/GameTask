package com.example.game.domain.usecase

import com.example.game.domain.model.GameDetails
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

class GetGameDetailsUseCaseTest {

    @MockK
    private lateinit var repository: GamesRepository

    private lateinit var useCase: GetGameDetailsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        useCase = GetGameDetailsUseCase(repository)
    }

    @Test
    fun invoke_returnsGameDetailsOnSuccess() = runTest {
        // Given
        val details: GameDetails = mockk(relaxed = true) {
            every { name } returns "GTA V"
            every { rating } returns 4.5
            every { metacritic } returns 92
            every { screenshots } returns listOf("url1", "url2")
            every { trailerUrl } returns "trailer_url"
        }
        coEvery { repository.getGameDetails(1) } returns Result.success(details)

        // When
        val result = useCase(1)

        // Then
        assertThat(result.isSuccess, `is`(true))
        assertThat(result.getOrNull()?.name, `is`("GTA V"))
        assertThat(result.getOrNull()?.metacritic, `is`(92))
        assertThat(result.getOrNull()?.screenshots?.size, `is`(2))
        coVerify(exactly = 1) { repository.getGameDetails(1) }
    }

    @Test
    fun invoke_returnsFailureOnError() = runTest {
        // Given
        coEvery { repository.getGameDetails(999) } returns Result.failure(Exception("Not found"))

        // When
        val result = useCase(999)

        // Then
        assertThat(result.isFailure, `is`(true))
        assertThat(result.exceptionOrNull()?.message, `is`("Not found"))
    }
}