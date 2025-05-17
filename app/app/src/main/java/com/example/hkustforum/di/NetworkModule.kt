package com.example.hkustforum.di

import com.example.hkustforum.data.remote.AuthInterceptor
import com.example.hkustforum.data.remote.ForumApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "http://10.0.2.2:8080/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideOkHttp(auth: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(auth)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideForumApi(retrofit: Retrofit): ForumApi =
        retrofit.create(ForumApi::class.java)
}
