package com.example.niftylive.data.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import com.squareup.moshi.Moshi

class RetrofitProvider {

    private val logging = HttpLoggingInterceptor { message ->
        Log.d("SmartAPI_RAW", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val errorCatcher = Interceptor { chain ->
        val request = chain.request()
        val response = chain.proceed(request)
        val bodyStr = response.body?.string() ?: ""
        Log.e("SmartAPI_RAW", "Response body: $bodyStr")

        // ✅ Wrap non-JSON responses in quotes so Moshi doesn’t crash
        val safeBody = if (bodyStr.trim().startsWith("{") || bodyStr.trim().startsWith("[")) {
            bodyStr
        } else {
            "{\"raw\":\"${bodyStr.replace("\"", "\\\"")}\"}"
        }

        response.newBuilder()
            .body(okhttp3.ResponseBody.create(response.body?.contentType(), safeBody))
            .build()
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(errorCatcher)
        .build()

    private val moshi = Moshi.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://apiconnect.angelone.in/") // ✅ SmartAPI base URL
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create()) // Handles plain text responses
        .addConverterFactory(MoshiConverterFactory.create(moshi)) // Handles JSON
        .build()

    val service: SmartApiService = retrofit.create(SmartApiService::class.java)
}