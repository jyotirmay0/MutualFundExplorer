package com.example.fundexplorer.di


import com.example.fundexplorer.Reposatory.MutualFundRepository
import com.example.fundexplorer.Reposatory.MutualFundRepositoryImpl
import com.example.fundexplorer.data.MutualFundApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideMutualFundApi(client: OkHttpClient): MutualFundApi {
        return Retrofit.Builder()
            .baseUrl("https://api.mfapi.in/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MutualFundApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMutualFundRepository(api: MutualFundApi): MutualFundRepository {
        return MutualFundRepositoryImpl(api)
    }
}