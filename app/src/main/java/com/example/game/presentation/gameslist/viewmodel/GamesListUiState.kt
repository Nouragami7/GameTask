package com.example.game.presentation.gameslist.viewmodel

import com.example.game.domain.model.Game
import com.example.game.domain.model.Genre

data class GamesListUiState(
    val games: List<Game> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val selectedGenre: Genre? = null,
    val searchQuery: String = "",
    val isInitialLoading: Boolean = true,
    val isPaginationLoading: Boolean = false,
    val initialError: String? = null,
    val paginationError: String? = null,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true
) {
    val filteredGames: List<Game>
        get() {
            val unique = games.distinctBy { it.id }
            return if (searchQuery.isBlank()) {
                unique
            } else {
                unique.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }
        }

    val isSearchEmpty: Boolean
        get() = searchQuery.isNotBlank() && filteredGames.isEmpty() && games.isNotEmpty()

    val isContentEmpty: Boolean
        get() = !isInitialLoading && initialError == null && games.isEmpty()
}