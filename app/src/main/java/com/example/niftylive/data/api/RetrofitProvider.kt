package com.example.niftylive.data.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitProvider {

    // ✅ SmartAPI Base URL (Angel One)
    private const val BASE_URL = "https://apiconnect.angelbroking.com/rest/"

    // ✅ Logging Interceptor for debugging
    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("SMARTAPI_HTTP", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // ✅ Header Interceptor — ensures SmartAPI always gets proper headers
    private val headerInterceptor = Interceptor { chain ->
        val original: Request = chain.request()
        val newRequest = original.newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .build()
        chain.proceed(newRequest)
    }

    // ✅ Build the OkHttp client
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(loggingInterceptor) // comment this line out for production
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    // ✅ Build Retrofit instance
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    // ✅ Expose API service (Singleton)
    val api: SmartApiService by lazy {
        retrofit.create(SmartApiService::class.java)
    }

    // ✅ Optional: quick test method for debugging
    @Throws(IOException::class)
    fun testConnection() {
        try {
            val response = client.newCall(
                Request.Builder().url(BASE_URL).build()
            ).execute()
            Log.d("SMARTAPI_TEST", "Response: ${response.code} ${response.message}")
        } catch (e: Exception) {
            Log.e("SMARTAPI_TEST", "Error testing connection: ${e.localizedMessage}")
        }
    }
}