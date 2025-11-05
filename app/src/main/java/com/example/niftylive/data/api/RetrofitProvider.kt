package com.example.niftylive.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitProvider {
    private val client = OkHttpClient.Builder().build()

    // TODO: Replace baseUrl with official SmartAPI REST base if different.
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://apiconnect.angelbroking.com/") // placeholder base
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val service: SmartApiService = retrofit.create(SmartApiService::class.java)
}