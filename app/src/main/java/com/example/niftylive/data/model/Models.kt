package com.example.niftylive.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ✅ Root response for login API
@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "status") val status: String? = null,
    @Json(name = "data") val data: LoginData? = null,
    @Json(name = "message") val message: String? = null
)

// ✅ Inner login data payload from SmartAPI
@JsonClass(generateAdapter = true)
data class LoginData(
    @Json(name = "access_token") val access_token: String? = null,
    @Json(name = "refresh_token") val refresh_token: String? = null,
    @Json(name = "feed_token") val feed_token: String? = null,
    @Json(name = "expires_in") val expires_in: Long? = null
)

// ✅ Response for quote API
@JsonClass(generateAdapter = true)
data class QuoteResponse(
    @Json(name = "status") val status: String? = null,
    @Json(name = "data") val data: Map<String, InstrumentQuote>? = null
)

// ✅ Actual instrument quote data (used in dashboard)
@JsonClass(generateAdapter = true)
data class InstrumentQuote(
    @Json(name = "token") val token: String? = null,
    @Json(name = "trading_symbol") val trading_symbol: String? = null,
    @Json(name = "last_traded_price") val last_traded_price: Double? = null,
    @Json(name = "close_price") val close_price: Double? = null,
    @Json(name = "change") val change: Double? = null,
    @Json(name = "percent_change") val percent_change: Double? = null,
    @Json(name = "volume") val volume: Long? = null
)