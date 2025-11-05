package com.example.niftylive.data.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import com.squareup.moshi.Moshi

class RetrofitProvider {

    private val logging = HttpLoggingInterceptor { message ->
        Log.d("SmartAPI_HTTP", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Use regular Moshi instance (no broken factory)
    private val moshi: Moshi = Moshi.Builder().build()

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        // Interceptor to log the raw response body and then rebuild it
        .addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                val response = chain.proceed(request)
                val body = response.body
                val bodyString = try {
                    body?.string() ?: ""
                } catch (e: Exception) {
                    Log.e("SmartAPI_RAW", "Error reading response body: ${e.localizedMessage}")
                    ""
                }

                // Log raw response text (important for debugging invalid JSON)
                Log.i("SmartAPI_RAW", bodyString)

                // Re-create response body (MediaType may be null)
                val contentType: MediaType? = body?.contentType()
                val newBody = ResponseBody.create(contentType, bodyString)

                return response.newBuilder()
                    .body(newBody)
                    .build()
            }
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://apiconnect.angelone.in/") // SmartAPI base (adjust if needed)
        .client(client)
        // Scalars first — lets plain text pass through without JSON parse error
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: SmartApiService = retrofit.create(SmartApiService::class.java)
}