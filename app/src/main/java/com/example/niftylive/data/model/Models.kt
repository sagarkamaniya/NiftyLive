package com.example.niftylive.data.model

import com.google.gson.annotations.SerializedName

// --- REST API Models ---
data class LoginRequest(
    @SerializedName("uid") val userId: String,
    @SerializedName("pwd") val password: String,
    @SerializedName("factor2") val totp: String,
    @SerializedName("vc") val vendorCode: String,
    @SerializedName("appkey") val appKey: String, // Note: Shoonya requires this to be a SHA256 hash of (userId|api_key)
    @SerializedName("imei") val imei: String = "niftylive_app",
    @SerializedName("source") val source: String = "API"
)

data class LoginResponse(
    @SerializedName("stat") val status: String, // "Ok" or "Not_Ok"
    @SerializedName("susertoken") val sessionToken: String?,
    @SerializedName("emsg") val errorMessage: String?
)

// --- WebSocket Models ---
data class WsInitMessage(
    @SerializedName("t") val task: String = "c",
    @SerializedName("uid") val userId: String,
    @SerializedName("actid") val accountId: String,
    @SerializedName("susertoken") val token: String,
    @SerializedName("source") val source: String = "API"
)

data class WsSubMessage(
    @SerializedName("t") val task: String = "t",
    @SerializedName("k") val keys: String // Shoonya format: "NSE|26000#NFO|54321"
)

data class MarketTick(
    @SerializedName("t") val type: String?, // 'tk' or 'tf'
    @SerializedName("e") val exchange: String?,
    @SerializedName("tk") val token: String?,
    @SerializedName("lp") val lastPrice: String?,
    @SerializedName("pc") val percentChange: String?
)
