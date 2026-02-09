package com.example.game.di

import android.content.Context
import androidx.room.Room
import com.example.game.BuildConfig
import com.example.game.data.local.dao.GamesDao
import com.example.game.data.local.db.GamesDatabase
import com.example.game.data.remote.api.GameApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://api.rawg.io/api/"
    private const val API_KEY = BuildConfig.RAWG_API_KEY

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val apiKeyInterceptor = Interceptor { chain ->
            val original = chain.request()
            val url = original.url.newBuilder()
                .addQueryParameter("key", API_KEY)
                .build()
            val request = original.newBuilder()
                .url(url)
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(apiKeyInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRawgApiService(retrofit: Retrofit): GameApiService {
        return retrofit.create(GameApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGamesDatabase(@ApplicationContext context: Context): GamesDatabase {
        return Room.databaseBuilder(
            context,
            GamesDatabase::class.java,
            "games_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideGamesDao(database: GamesDatabase): GamesDao {
        return database.gamesDao()
    }
}