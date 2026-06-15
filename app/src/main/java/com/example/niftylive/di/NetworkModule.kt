package com.example.niftylive.di

import com.example.niftylive.data.api.SmartApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideShoonyaInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val body = request.body
            val builder = request.newBuilder().header("Content-Type", "application/json")
            
            // Core logic: Shoonya requires raw JSON prepended with "jData="
            if (body != null && request.method == "POST") {
                val buffer = Buffer()
                body.writeTo(buffer)
                val json = buffer.readUtf8()
                builder.post("jData=$json".toRequestBody(body.contentType()))
            }
            chain.proceed(builder.build())
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideSmartApiService(client: OkHttpClient): SmartApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.shoonya.com/NorenWClientTP/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SmartApiService::class.java)
    }
}
