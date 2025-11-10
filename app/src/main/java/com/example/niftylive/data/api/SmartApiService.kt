package com.example.app.network

import com.example.app.models.NiftyDataModel
import com.example.app.models.StockQuote
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface for the SmartApiService, defining all network API endpoints.
 * This is used by Retrofit to create a network client.
 */
interface SmartApiService {

    /**
     * Fetches the main Nifty index data.
     * Example: GET /api/v1/market/nifty50
     */
    @GET("api/v1/market/nifty50")
    suspend fun getNiftyIndex(
        @Header("Authorization") token: String
    ): Response<NiftyDataModel>

    /**
     * Fetches a specific stock's quote using a symbol in the URL path.
     * Example: GET /api/v1/stocks/RELIANCE
     */
    @GET("api/v1/stocks/{symbol}")
    suspend fun getStockQuote(
        @Header("Authorization") token: String,
        @Path("symbol") stockSymbol: String
    ): Response<StockQuote>

    /**
     * Searches for stocks using a query parameter.
     * Example: GET /api/v1/search?q=tata
     */
    @GET("api/v1/search")
    suspend fun searchStocks(
        @Header("Authorization") token: String,
        @Query("q") searchQuery: String
    ): Response<List<StockQuote>>

    // You could add other endpoints like for user portfolios, orders, etc.
    //
    // @POST("api/v1/order")
    // suspend fun placeOrder(@Body orderRequest: OrderRequest): Response<OrderResponse>
}
