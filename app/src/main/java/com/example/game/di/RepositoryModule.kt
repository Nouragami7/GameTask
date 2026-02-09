package com.example.game.di

import com.example.game.data.repository.GamesRepositoryImpl
import com.example.game.domain.repository.GamesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGamesRepository(
        impl: GamesRepositoryImpl
    ): GamesRepository
}