package com.example.niftylive.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// --- LOGIN MODELS (Already updated, but good to have in one file) ---

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "status") val status: Boolean? = null,
    @Json(name = "data") val data: LoginData? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "errorcode") val errorcode: String? = null
)

@JsonClass(generateAdapter = true)
data class LoginData(
    @Json(name = "jwtToken") val jwtToken: String? = null,
    @Json(name = "refreshToken") val refreshToken: String? = null,
    @Json(name = "feedToken") val feedToken: String? = null
)


// --- QUOTE MODELS (New and Updated) ---

// ✅ This is the main, top-level response for the quote API
@JsonClass(generateAdapter = true)
data class QuoteResponse(
    @Json(name = "status") val status: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "errorcode") val errorcode: String? = null,
    @Json(name = "data") val data: MarketData? = null
)

// ✅ This holds the 'fetched' and 'unfetched' lists
@JsonClass(generateAdapter = true)
data class MarketData(
    @Json(name = "fetched") val fetched: List<InstrumentQuote>,
    @Json(name = "unfetched") val unfetched: List<UnfetchedQuote>? = null
)

// ✅ This is the actual data for one instrument (replaces your old model)
// Note the new field names (ltp, percentChange, etc.)
@JsonClass(generateAdapter = true)
data class InstrumentQuote(
    @Json(name = "exchange") val exchange: String? = null,
    @Json(name = "tradingSymbol") val tradingSymbol: String? = null,
    @Json(name = "symbolToken") val symbolToken: String? = null,
    @Json(name = "ltp") val ltp: Double? = null,
    @Json(name = "open") val open: Double? = null,
    @Json(name = "high") val high: Double? = null,
    @Json(name = "low") val low: Double? = null,
    @Json(name = "close") val close: Double? = null,
    @Json(name = "netChange") val netChange: Double? = null,
    @Json(name = "percentChange") val percentChange: Double? = null,
    @Json(name = "tradeVolume") val tradeVolume: Long? = null,
    @Json(name = "avgPrice") val avgPrice: Double? = null,
    @Json(name = "52WeekHigh") val fiftyTwoWeekHigh: Double? = null,
    @Json(name = "52WeekLow") val fiftyTwoWeekLow: Double? = null
)

// ✅ This is for any symbols that failed to fetch
@JsonClass(generateAdapter = true)
data class UnfetchedQuote(
    @Json(name = "exchange") val exchange: String? = null,
    @Json(name = "symbolToken") val symbolToken: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "errorCode") val errorCode: String? = null
)
