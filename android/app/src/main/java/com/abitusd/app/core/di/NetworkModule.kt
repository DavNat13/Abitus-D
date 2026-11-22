package com.abitusd.app.core.di

import com.abitusd.app.BuildConfig
import com.abitusd.app.core.network.AccountApi
import com.abitusd.app.core.network.AuthInterceptor
import com.abitusd.app.core.network.DataApi
import com.abitusd.app.core.network.PublicAuthApi
import com.abitusd.app.core.network.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Authenticated

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TIMEOUT_SECONDS = 20L
    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

    @Provides
    @Singleton
    fun baseClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @Authenticated
    fun authenticatedClient(
        base: OkHttpClient,
        interceptor: AuthInterceptor,
        authenticator: TokenAuthenticator,
    ): OkHttpClient =
        base
            .newBuilder()
            .addInterceptor(interceptor)
            .authenticator(authenticator)
            .build()

    @Provides
    @Singleton
    fun publicAuthApi(client: OkHttpClient): PublicAuthApi = retrofit(client).create(PublicAuthApi::class.java)

    @Provides
    @Singleton
    fun accountApi(
        @Authenticated client: OkHttpClient,
    ): AccountApi = retrofit(client).create(AccountApi::class.java)

    @Provides
    @Singleton
    fun dataApi(
        @Authenticated client: OkHttpClient,
    ): DataApi = retrofit(client).create(DataApi::class.java)

    private fun retrofit(client: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
}
