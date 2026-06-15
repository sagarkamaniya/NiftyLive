package com.example.niftylive.data.model

import com.google.gson.annotations.SerializedName

// ==========================================
// 1. SHOONYA REST API MODELS
// ==========================================
data class LoginRequest(
    @SerializedName("uid") val userId: String,
    @SerializedName("pwd") val password: String,
    @SerializedName("factor2") val totp: String,
    @SerializedName("vc") val vendorCode: String,
    @SerializedName("appkey") val appKey: String,
    @SerializedName("imei") val imei: String = "niftylive_app",
    @SerializedName("source") val source: String = "API"
)

data class LoginResponse(
    @SerializedName("stat") val status: String, // "Ok" or "Not_Ok"
    @SerializedName("susertoken") val sessionToken: String?,
    @SerializedName("emsg") val errorMessage: String?
)

// ==========================================
// 2. SHOONYA WEBSOCKET MODELS
// ==========================================
data class WsInitMessage(
    @SerializedName("t") val task: String = "c",
    @SerializedName("uid") val userId: String,
    @SerializedName("actid") val accountId: String,
    @SerializedName("susertoken") val token: String,
    @SerializedName("source") val source: String = "API"
)

data class WsSubMessage(
    @SerializedName("t") val task: String = "t",
    @SerializedName("k") val keys: String // Format: "NSE|26000#NFO|54321"
)

data class MarketTick(
    @SerializedName("t") val type: String?, // 'tk' or 'tf'
    @SerializedName("e") val exchange: String?,
    @SerializedName("tk") val token: String?,
    @SerializedName("lp") val lastPrice: String?,
    @SerializedName("pc") val percentChange: String?
)

// ==========================================
// 3. DASHBOARD UI MODELS (Restored)
// ==========================================
data class Holding(
    val tradingSymbol: String? = null,
    val symbolToken: String? = null,
    val averagePrice: Double? = null,
    val quantity: Int? = null,
    var ltp: Double? = null,
    var pnl: Double? = null
)

data class InstrumentQuote(
    val tradingSymbol: String? = null,
    val symbolToken: String? = null,
    val ltp: Double? = null,
    val netChange: Double? = null,
    val percentChange: Double? = null
)

data class OrderRequest(
    val tradingSymbol: String,
    val symbolToken: String,
    val transactionType: String,
    val exchange: String,
    val orderType: String,
    val productType: String,
    val quantity: String,
    val price: String
)
