package com.example.niftylive.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val status: String? = null,
    val data: LoginData? = null
)

@JsonClass(generateAdapter = true)
data class LoginData(
    val access_token: String? = null,
    val refresh_token: String? = null,
    val feed_token: String? = null,
    val expires_in: Long? = null
)

@JsonClass(generateAdapter = true)
data class QuoteResponse(
    val status: String? = null,
    val data: Map<String, InstrumentQuote>? = null
)

@JsonClass(generateAdapter = true)
data class InstrumentQuote(
    val lastPrice: Double? = null,
    val closePrice: Double? = null,
    val change: Double? = null,
    val percentChange: Double? = null,
    val instrumentToken: String? = null
)
