package com.example.niftylive.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitProvider {
    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://apiconnect.angelone.in/") // <- replace with the real SmartAPI base if different
        .client(client)
        // Scalars FIRST so plain text/HTML responses are handled as String
        .addConverterFactory(ScalarsConverterFactory.create())
        // then Moshi for real JSON
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val moshiInstance: Moshi = moshi
}