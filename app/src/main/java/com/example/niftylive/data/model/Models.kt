package com.example.niftylive.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ✅ Top-level SmartAPI login response
@JsonClass(generateAdapter = true)
data class LoginResponse(
    val status: String? = null,
    val message: String? = null,
    val data: LoginData? = null
)

// ✅ Nested token data returned by SmartAPI
@JsonClass(generateAdapter = true)
data class LoginData(
    @Json(name = "jwtToken") val access_token: String? = null,
    @Json(name = "refreshToken") val refresh_token: String? = null,
    @Json(name = "feedToken") val feed_token: String? = null,
    val expires_in: Long? = null
)

// ✅ Quote API response
@JsonClass(generateAdapter = true)
data class QuoteResponse(
    val status: String? = null,
    val data: Map<String, InstrumentQuote>? = null
)

// ✅ Quote instrument data
@JsonClass(generateAdapter = true)
data class InstrumentQuote(
    val lastPrice: Double? = null,
    val closePrice: Double? = null,
    val change: Double? = null,
    val percentChange: Double? = null,
    val instrumentToken: String? = null
)