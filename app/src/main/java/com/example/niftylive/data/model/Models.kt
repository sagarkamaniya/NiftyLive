package com.example.niftylive.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// --- LOGIN MODELS ---
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

// --- QUOTE MODELS ---
@JsonClass(generateAdapter = true)
data class QuoteResponse(
    @Json(name = "status") val status: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "errorcode") val errorcode: String? = null,
    @Json(name = "data") val data: MarketData? = null
)

@JsonClass(generateAdapter = true)
data class MarketData(
    @Json(name = "fetched") val fetched: List<InstrumentQuote>,
    @Json(name = "unfetched") val unfetched: List<UnfetchedQuote>? = null
)

@JsonClass(generateAdapter = true)
data class InstrumentQuote(
    @Json(name = "exchange") val exchange: String? = null,
    @Json(name = "tradingSymbol") val tradingSymbol: String? = null,
    @Json(name = "symbolToken") val symbolToken: String? = null,
    @Json(name = "ltp") val ltp: Double? = null,
    @Json(name = "netChange") val netChange: Double? = null,
    @Json(name = "percentChange") val percentChange: Double? = null
)

@JsonClass(generateAdapter = true)
data class UnfetchedQuote(
    @Json(name = "symbolToken") val symbolToken: String? = null,
    @Json(name = "message") val message: String? = null
)

// --- QUOTE REQUEST MODELS ---
@JsonClass(generateAdapter = true)
data class QuoteRequest(
    @Json(name = "mode") val mode: String,
    @Json(name = "exchangeTokens") val exchangeTokens: ExchangeTokens
)

@JsonClass(generateAdapter = true)
data class ExchangeTokens(
    @Json(name = "NSE") val nse: List<String>
)

// --- PROFILE MODELS ---
@JsonClass(generateAdapter = true)
data class ProfileResponse(
    @Json(name = "status") val status: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: ProfileData? = null
)

@JsonClass(generateAdapter = true)
data class ProfileData(
    @Json(name = "clientcode") val clientCode: String? = null,
    @Json(name = "name") val name: String? = null
)

// ✅ UPDATED PORTFOLIO MODELS (Using standard camelCase keys)
@JsonClass(generateAdapter = true)
data class HoldingResponse(
    @Json(name = "status") val status: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: List<Holding>? = null
)

@JsonClass(generateAdapter = true)
data class Holding(
    @Json(name = "tradingSymbol") val tradingSymbol: String? = null,
    @Json(name = "symbolToken") val symbolToken: String? = null,
    @Json(name = "exchange") val exchange: String? = null,
    @Json(name = "isin") val isin: String? = null,
    @Json(name = "quantity") val quantity: Long? = null,
    @Json(name = "averagePrice") val averagePrice: Double? = null,
    @Json(name = "ltp") val ltp: Double? = null,
    @Json(name = "profitAndLoss") val pnl: Double? = null, // Note: Check Logcat if this is null
    @Json(name = "product") val product: String? = null
)
