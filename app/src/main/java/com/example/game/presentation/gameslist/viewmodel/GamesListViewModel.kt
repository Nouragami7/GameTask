package com.example.game.presentation.gameslist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.game.domain.model.Genre
import com.example.game.domain.usecase.GetGamesByGenreUseCase
import com.example.game.domain.usecase.GetGenresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamesListViewModel @Inject constructor(
    private val getGenresUseCase: GetGenresUseCase,
    private val getGamesByGenreUseCase: GetGamesByGenreUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GamesListUiState())
    val uiState: StateFlow<GamesListUiState> = _uiState.asStateFlow()

    init {
        loadGenres()
    }

    fun loadGenres() {
        viewModelScope.launch {
            getGenresUseCase().onSuccess { genres ->
                _uiState.update { it.copy(genres = genres) }
                if (genres.isNotEmpty()) {
                    selectGenre(genres.first())
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isInitialLoading = false,
                        initialError = error.message ?: "Failed to load genres"
                    )
                }
            }
        }
    }

    fun selectGenre(genre: Genre) {
        _uiState.update {
            it.copy(
                selectedGenre = genre,
                games = emptyList(),
                currentPage = 1,
                hasMorePages = true,
                searchQuery = "",
                initialError = null
            )
        }
        loadGames()
    }

    private fun loadGames() {
        viewModelScope.launch {
            _uiState.update { it.copy(isInitialLoading = true, initialError = null) }
            val genre = _uiState.value.selectedGenre?.slug ?: return@launch

            getGamesByGenreUseCase(genre, page = 1).onSuccess { games ->
                _uiState.update {
                    it.copy(
                        games = games,
                        isInitialLoading = false,
                        currentPage = 1,
                        hasMorePages = games.size >= 20
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isInitialLoading = false,
                        initialError = error.message ?: "Failed to load games"
                    )
                }
            }
        }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isPaginationLoading || !state.hasMorePages || state.isInitialLoading) return

        viewModelScope.launch {
            val nextPage = state.currentPage + 1
            val genre = state.selectedGenre?.slug ?: return@launch

            _uiState.update { it.copy(isPaginationLoading = true, paginationError = null) }

            getGamesByGenreUseCase(genre, page = nextPage).onSuccess { newGames ->
                _uiState.update {
                    it.copy(
                        games = it.games + newGames,
                        currentPage = nextPage,
                        isPaginationLoading = false,
                        hasMorePages = newGames.size >= 20
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isPaginationLoading = false,
                        paginationError = error.message ?: "Failed to load more games"
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun retry() {
        if (_uiState.value.genres.isEmpty()) {
            loadGenres()
        } else {
            loadGames()
        }
    }

    fun retryPagination() {
        loadNextPage()
    }
}