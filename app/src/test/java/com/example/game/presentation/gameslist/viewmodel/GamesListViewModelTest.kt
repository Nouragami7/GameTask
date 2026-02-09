package com.example.game.presentation.gameslist.viewmodel

import com.example.game.domain.model.Game
import com.example.game.domain.model.Genre
import com.example.game.domain.usecase.GetGamesByGenreUseCase
import com.example.game.domain.usecase.GetGenresUseCase
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
class GamesListViewModelTest {

    @MockK
    private lateinit var getGenresUseCase: GetGenresUseCase

    @MockK
    private lateinit var getGamesByGenreUseCase: GetGamesByGenreUseCase

    private lateinit var viewModel: GamesListViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val fakeGenres = listOf(
        mockk<Genre>(relaxed = true) {
            every { name } returns "Action"
            every { slug } returns "action"
        },
        mockk<Genre>(relaxed = true) {
            every { name } returns "RPG"
            every { slug } returns "rpg"
        },
        mockk<Genre>(relaxed = true) {
            every { name } returns "Shooter"
            every { slug } returns "shooter"
        }
    )

    private val fakeGames = listOf(
        mockk<Game>(relaxed = true) {
            every { id } returns 1
            every { name } returns "GTA V"
            every { rating } returns 4.5
        },
        mockk<Game>(relaxed = true) {
            every { id } returns 2
            every { name } returns "Witcher 3"
            every { rating } returns 4.7
        },
        mockk<Game>(relaxed = true) {
            every { id } returns 3
            every { name } returns "Halo"
            every { rating } returns 4.2
        }
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): GamesListViewModel {
        return GamesListViewModel(getGenresUseCase, getGamesByGenreUseCase)
    }

    @Test
    fun initialState_isLoading() = runTest {
        // Given
        coEvery { getGenresUseCase() } returns Result.success(fakeGenres)
        coEvery { getGamesByGenreUseCase(any(), any()) } returns Result.success(fakeGames)

        // When
        viewModel = createViewModel()

        // Then
        assertThat(viewModel.uiState.value.isInitialLoading, `is`(true))
    }

    @Test
    fun loadGenres_updatesGenresState() = runTest {
        // Given
        coEvery { getGenresUseCase() } returns Result.success(fakeGenres)
        coEvery { getGamesByGenreUseCase(any(), any()) } returns Result.success(fakeGames)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.genres.size, `is`(3))
        assertThat(state.selectedGenre, `is`(notNullValue()))
    }

    @Test
    fun loadGenres_autoSelectsFirstAndLoadsGames() = runTest {
        // Given
        coEvery { getGenresUseCase() } returns Result.success(fakeGenres)
        coEvery { getGamesByGenreUseCase(any(), any()) } returns Result.success(fakeGames)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.isInitialLoading, `is`(false))
        assertThat(state.games.size, `is`(3))
    }

    @Test
    fun selectGenre_loadsNewGames() = runTest {
        // Given
        coEvery { getGenresUseCase() } returns Result.success(fakeGenres)
        coEvery { getGamesByGenreUseCase(any(), any()) } returns Result.success(fakeGames)

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.selectGenre(fakeGenres[1])
        advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.selectedGenre, `is`(fakeGenres[1]))
    }

    @Test
    fun searchQuery_filtersGamesLocally() = runTest {
        // Given
        coEvery { getGenresUseCase() } returns Result.success(fakeGenres)
        coEvery { getGamesByGenreUseCase(any(), any()) } returns Result.success(fakeGames)

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChanged("GTA")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.searchQuery, `is`("GTA"))
        assertThat(state.filteredGames.size, `is`(1))
        assertThat(state.filteredGames.first().name, `is`("GTA V"))
    }

    @Test
    fun searchQuery_noMatchShowsEmpty() = runTest {
        // Given
        coEvery { getGenresUseCase() } returns Result.success(fakeGenres)
        coEvery { getGamesByGenreUseCase(any(), any()) } returns Result.success(fakeGames)

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChanged("xyz123")

        // Then
        assertThat(viewModel.uiState.value.isSearchEmpty, `is`(true))
        assertThat(viewModel.uiState.value.filteredGames.isEmpty(), `is`(true))
    }

    @Test
    fun loadGenres_failure_updatesErrorState() = runTest {
        // Given
        coEvery { getGenresUseCase() } returns Result.failure(Exception("Network error"))

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.isInitialLoading, `is`(false))
        assertThat(state.initialError, `is`("Network error"))
    }

    @Test
    fun retry_reloadsAfterFailure() = runTest {
        // Given
        coEvery { getGenresUseCase() } returns Result.failure(Exception("Network error"))

        viewModel = createViewModel()
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.initialError, `is`(notNullValue()))

        // When
        coEvery { getGenresUseCase() } returns Result.success(fakeGenres)
        coEvery { getGamesByGenreUseCase(any(), any()) } returns Result.success(fakeGames)
        viewModel.retry()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.initialError, `is`(nullValue()))
        assertThat(state.genres.size, `is`(3))
    }

}