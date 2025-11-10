package com.example.niftylive.di

import com.example.niftylive.data.api.SmartApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 1. Teach Hilt how to make the logging interceptor
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // 2. Teach Hilt how to make the OkHttpClient
    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // Add the logger
            .build()
        // Hilt now knows how to provide this to OkHttpWsClient
    }

    // 3. Teach Hilt how to make Moshi (for JSON parsing)
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    // 4. Teach Hilt how to make Retrofit
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://apiconnect.angelbroking.com/")
            .client(client) // Use the client from step 2
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi)) // Use the Moshi from step 3
            .build()
    }

    // 5. Teach Hilt how to make your SmartApiService
    @Provides
    @Singleton
    fun provideSmartApiService(retrofit: Retrofit): SmartApiService {
        return retrofit.create(SmartApiService::class.java)
        // Now Hilt can inject this into your NiftyRepository!
    }
}
