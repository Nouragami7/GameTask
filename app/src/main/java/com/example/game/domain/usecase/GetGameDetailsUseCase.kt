package com.example.game.domain.usecase

import com.example.game.domain.model.GameDetails
import com.example.game.domain.repository.GamesRepository
import javax.inject.Inject

class GetGameDetailsUseCase @Inject constructor(
    private val repository: GamesRepository
) {
    suspend operator fun invoke(gameId: Int): Result<GameDetails> {
        return repository.getGameDetails(gameId)
    }
}