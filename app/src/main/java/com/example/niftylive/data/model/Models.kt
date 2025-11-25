package com.example.niftylive.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// --- LOGIN & QUOTE ---
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

@JsonClass(generateAdapter = true)
data class QuoteRequest(
    @Json(name = "mode") val mode: String,
    @Json(name = "exchangeTokens") val exchangeTokens: ExchangeTokens
)

@JsonClass(generateAdapter = true)
data class ExchangeTokens(
    @Json(name = "NSE") val nse: List<String>
)

// --- PROFILE ---
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

// --- PORTFOLIO ---
@JsonClass(generateAdapter = true)
data class HoldingResponse(
    @Json(name = "status") val status: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: PortfolioData? = null,
    @Json(name = "errorcode") val errorcode: String? = null
)

@JsonClass(generateAdapter = true)
data class PortfolioData(
    @Json(name = "holdings") val holdings: List<Holding>? = null,
    @Json(name = "totalholding") val totalHolding: TotalHolding? = null
)

@JsonClass(generateAdapter = true)
data class TotalHolding(
    @Json(name = "totalholdingvalue") val totalValue: Double? = null,
    @Json(name = "totalprofitandloss") val totalPnl: Double? = null,
    @Json(name = "totalpnlpercentage") val totalPnlPercentage: Double? = null
)

@JsonClass(generateAdapter = true)
data class Holding(
    @Json(name = "tradingsymbol") val tradingSymbol: String? = null,
    @Json(name = "symboltoken") val symbolToken: String? = null,
    @Json(name = "exchange") val exchange: String? = null,
    @Json(name = "isin") val isin: String? = null,
    @Json(name = "quantity") val quantity: Long? = null,
    @Json(name = "averageprice") val averagePrice: Double? = null,
    @Json(name = "ltp") val ltp: Double? = null,
    @Json(name = "profitandloss") val pnl: Double? = null,
    @Json(name = "product") val product: String? = null
)

// --- FUNDS (RMS) ---
@JsonClass(generateAdapter = true)
data class RMSResponse(
    @Json(name = "status") val status: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "errorcode") val errorcode: String? = null,
    @Json(name = "data") val data: RMSData? = null
)

@JsonClass(generateAdapter = true)
data class RMSData(
    @Json(name = "net") val net: String? = null,
    @Json(name = "availablecash") val availableCash: String? = null
)

// --- ORDERS ---
@JsonClass(generateAdapter = true)
data class OrderRequest(
    @Json(name = "variety") val variety: String = "NORMAL",
    @Json(name = "tradingsymbol") val tradingSymbol: String,
    @Json(name = "symboltoken") val symbolToken: String,
    @Json(name = "transactiontype") val transactionType: String,
    @Json(name = "exchange") val exchange: String,
    @Json(name = "ordertype") val orderType: String,
    @Json(name = "producttype") val productType: String,
    @Json(name = "duration") val duration: String = "DAY",
    @Json(name = "price") val price: String = "0",
    @Json(name = "quantity") val quantity: String
)

@JsonClass(generateAdapter = true)
data class OrderResponse(
    @Json(name = "status") val status: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: OrderResponseData? = null
)

@JsonClass(generateAdapter = true)
data class OrderResponseData(
    @Json(name = "orderid") val orderId: String? = null,
    @Json(name = "uniqueorderid") val uniqueOrderId: String? = null
)
