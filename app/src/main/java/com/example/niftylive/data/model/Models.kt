package com.example.niftylive.data.model

import com.squareup.moshi.JsonClass

/**
 * ✅ Matches SmartAPI login and quote JSON response formats.
 * Ensures proper Moshi parsing without "Expected BEGIN_OBJECT" errors.
 */

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val status: Boolean? = null,
    val message: String? = null,
    val data: LoginData? = null
)

@JsonClass(generateAdapter = true)
data class LoginData(
    val jwtToken: String? = null,
    val refreshToken: String? = null,
    val feedToken: String? = null,
    val publicToken: String? = null,
    val userId: String? = null
)

@JsonClass(generateAdapter = true)
data class QuoteResponse(
    val status: Boolean? = null,
    val message: String? = null,
    val data: Map<String, InstrumentQuote>? = null
)

@JsonClass(generateAdapter = true)
data class InstrumentQuote(
    val lastPrice: Double? = null,
    val closePrice: Double? = null,
    val change: Double? = null,
    val percentChange: Double? = null,
    val instrumentToken: String? = null,
    val exchange: String? = null
)