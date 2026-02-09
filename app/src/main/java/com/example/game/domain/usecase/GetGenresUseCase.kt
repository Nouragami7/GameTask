package com.example.game.domain.usecase

import com.example.game.domain.model.Genre
import com.example.game.domain.repository.GamesRepository
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(
    private val repository: GamesRepository
) {
    suspend operator fun invoke(): Result<List<Genre>> {
        return repository.getGenres()
    }
}