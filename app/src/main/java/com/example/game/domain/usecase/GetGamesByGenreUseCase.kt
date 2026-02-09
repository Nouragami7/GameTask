package com.example.game.domain.usecase

import com.example.game.domain.model.Game
import com.example.game.domain.repository.GamesRepository
import javax.inject.Inject

class GetGamesByGenreUseCase @Inject constructor(
    private val repository: GamesRepository
) {
    suspend operator fun invoke(genre: String, page: Int): Result<List<Game>> {
        return repository.getGames(genre, page)
    }
}