package com.example.niftylive.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ✅ UPDATED to match new API response
@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "status") val status: Boolean? = null,
    @Json(name = "data") val data: LoginData? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "errorcode") val errorcode: String? = null
)

// ✅ UPDATED to match new API response
@JsonClass(generateAdapter = true)
data class LoginData(
    @Json(name = "jwtToken") val jwtToken: String? = null,
    @Json(name = "refreshToken") val refreshToken: String? = null,
    @Json(name = "feedToken") val feedToken: String? = null
)

// ✅ This is your OLD Quote Response, it will likely need to be updated.
@JsonClass(generateAdapter = true)
data class QuoteResponse(
    @Json(name = "status") val status: String? = null,
    @Json(name = "data") val data: Map<String, InstrumentQuote>? = null
)

@JsonClass(generateAdapter = true)
data class InstrumentQuote(
    @Json(name = "token") val token: String? = null,
    @Json(name = "trading_symbol") val trading_symbol: String? = null,
    @Json(name = "last_traded_price") val last_traded_price: Double? = null,
    @Json(name = "close_price") val close_price: Double? = null,
    @Json(name = "change") val change: Double? = null,
    @Json(name =AN = "percent_change") val percent_change: Double? = null,
    @Json(name = "volume") val volume: Long? = null
)
