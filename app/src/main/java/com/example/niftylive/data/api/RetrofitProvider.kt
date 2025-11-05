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
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import java.io.StringReader

class RetrofitProvider {

    private val logging = HttpLoggingInterceptor { message ->
        Log.d("SmartAPI_HTTP", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Custom lenient Moshi setup
    private val moshi = Moshi.Builder()
        .add { type, annotations, moshi ->
            moshi.nextAdapter<Any>(this, type, annotations)
        }
        .build()

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        // Log raw response text for debugging
        .addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                val response = chain.proceed(request)
                val rawBody = response.body?.string() ?: ""
                Log.e("SmartAPI_RAW", rawBody)
                // rebuild the body because .string() consumes it
                return response.newBuilder()
                    .body(okhttp3.ResponseBody.create(response.body?.contentType(), rawBody))
                    .build()
            }
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://apiconnect.angelone.in/") // ✅ Official SmartAPI base URL
        .client(client)
        // Scalars first for plain-text or malformed JSON responses
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: SmartApiService = retrofit.create(SmartApiService::class.java)
}