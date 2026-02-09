package com.example.game.presentation.gamedetails.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.game.domain.usecase.GetGameDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailsViewModel @Inject constructor(
    private val getGameDetailsUseCase: GetGameDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val gameId: Int = savedStateHandle.get<Int>("gameId")
        ?: throw IllegalArgumentException("gameId is required")

    private val _uiState = MutableStateFlow(GameDetailsUiState())
    val uiState: StateFlow<GameDetailsUiState> = _uiState.asStateFlow()

    init {
        loadGameDetails()
    }

    fun loadGameDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getGameDetailsUseCase(gameId).onSuccess { details ->
                _uiState.update {
                    it.copy(gameDetails = details, isLoading = false)
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load game details"
                    )
                }
            }
        }
    }

    fun retry() = loadGameDetails()
}