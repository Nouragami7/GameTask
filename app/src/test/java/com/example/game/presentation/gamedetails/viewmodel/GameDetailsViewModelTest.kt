package com.example.game.presentation.gamedetails.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.game.domain.model.GameDetails
import com.example.game.domain.usecase.GetGameDetailsUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameDetailsViewModelTest {

    @MockK
    private lateinit var getGameDetailsUseCase: GetGameDetailsUseCase

    private lateinit var viewModel: GameDetailsViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val fakeDetails: GameDetails = mockk(relaxed = true) {
        every { id } returns 1
        every { name } returns "GTA"
        every { rating } returns 4.5
        every { releaseDate } returns "2013-09-17"
        every { description } returns "Open world action-adventure game."
        every { metacritic } returns 92
        every { genres } returns listOf("Action", "Adventure")
        every { screenshots } returns listOf("url1", "url2")
        every { trailerUrl } returns "https://youtube.com/trailer"
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(gameId: Int = 1): GameDetailsViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("gameId" to gameId))
        return GameDetailsViewModel(getGameDetailsUseCase, savedStateHandle)
    }

    @Test
    fun initialState_isLoading() = runTest {
        // Given
        coEvery { getGameDetailsUseCase(any()) } returns Result.success(fakeDetails)

        // When
        viewModel = createViewModel()

        // Then
        assertThat(viewModel.uiState.value.isLoading, `is`(true))
    }

    @Test
    fun loadDetails_updatesStateOnSuccess() = runTest {
        // Given
        coEvery { getGameDetailsUseCase(1) } returns Result.success(fakeDetails)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.isLoading, `is`(false))
        assertThat(state.error, `is`(nullValue()))
        assertThat(state.gameDetails, `is`(notNullValue()))
        assertThat(state.gameDetails?.name, `is`("GTA"))
        assertThat(state.gameDetails?.rating, `is`(4.5))
        assertThat(state.gameDetails?.metacritic, `is`(92))
        assertThat(state.gameDetails?.screenshots?.size, `is`(2))
        assertThat(state.gameDetails?.trailerUrl, `is`("https://youtube.com/trailer"))
    }

    @Test
    fun loadDetails_updatesErrorOnFailure() = runTest {
        // Given
        coEvery { getGameDetailsUseCase(999) } returns Result.failure(Exception("Game not found"))

        // When
        viewModel = createViewModel(gameId = 999)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.isLoading, `is`(false))
        assertThat(state.error, `is`("Game not found"))
        assertThat(state.gameDetails, `is`(nullValue()))
    }

    @Test
    fun retry_reloadsAfterFailure() = runTest {
        // Given
        coEvery { getGameDetailsUseCase(1) } returns Result.failure(Exception("Network error"))

        viewModel = createViewModel()
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.error, `is`(notNullValue()))

        // When
        coEvery { getGameDetailsUseCase(1) } returns Result.success(fakeDetails)
        viewModel.retry()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.error, `is`(nullValue()))
        assertThat(state.gameDetails?.name, `is`("GTA"))
    }

    @Test
    fun loadDetails_handlesNullMetacritic() = runTest {
        // Given
        val noMeta: GameDetails = mockk(relaxed = true) {
            every { metacritic } returns null
        }
        coEvery { getGameDetailsUseCase(1) } returns Result.success(noMeta)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.gameDetails?.metacritic, `is`(nullValue()))
    }

    @Test
    fun loadDetails_handlesEmptyScreenshots() = runTest {
        // Given
        val noScreenshots: GameDetails = mockk(relaxed = true) {
            every { screenshots } returns emptyList()
        }
        coEvery { getGameDetailsUseCase(1) } returns Result.success(noScreenshots)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.gameDetails?.screenshots?.isEmpty(), `is`(true))
    }

    @Test
    fun loadDetails_handlesNullTrailer() = runTest {
        // Given
        val noTrailer: GameDetails = mockk(relaxed = true) {
            every { trailerUrl } returns null
        }
        coEvery { getGameDetailsUseCase(1) } returns Result.success(noTrailer)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.gameDetails?.trailerUrl, `is`(nullValue()))
    }
}