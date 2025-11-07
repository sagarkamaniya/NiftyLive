package com.example.niftylive.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitProvider(
    baseUrl: String = "https://apiconnect.angelone.in/" // change if needed
) {
    private val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create()) // for text responses
        .addConverterFactory(MoshiConverterFactory.create())   // for JSON
        .build()

    val service: SmartApiService = retrofit.create(SmartApiService::class.java)
}