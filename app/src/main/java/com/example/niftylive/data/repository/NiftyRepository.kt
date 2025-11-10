package com.example.app.data

import com.example.app.models.NiftyDataModel
import com.example.app.models.StockQuote
import com.example.app.network.SmartApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository class to manage data for Nifty and stock information.
 *
 * It acts as the single source of truth for the app's data,
 * handling logic for fetching from the network (via ApiService)
 * or a local cache/database (not shown).
 *
 * @param apiService The Retrofit service interface injected by Hilt/Dagger.
 */
@Singleton
class NiftyRepository @Inject constructor(
    private val apiService: SmartApiService
) {

    // A hardcoded token for example purposes.
    // In a real app, this would be fetched from user preferences or a secure store.
    private val authToken = "Bearer YOUR_API_TOKEN_HERE"

    /**
     * Fetches Nifty index data and emits it as a Flow.
     * This uses a simple 'flow' builder to emit network results.
     */
    fun getNiftyIndexData(): Flow<Result<NiftyDataModel>> = flow {
        try {
            val response = apiService.getNiftyIndex(authToken)
            
            if (response.isSuccessful && response.body() != null) {
                // Emit the successful data
                emit(Result.success(response.body()!!))
            } else {
                // Emit a failure with an error message
                emit(Result.failure(Exception("Error fetching Nifty index: ${response.message()}")))
            }
        } catch (e: Exception) {
            // Emit a failure if a network exception occurs
            emit(Result.failure(e))
        }
    }

    /**
     * Fetches a specific stock quote and emits it as a Flow.
     *
     * @param symbol The stock symbol (e.g., "RELIANCE")
     */
    fun getStockQuoteData(symbol: String): Flow<Result<StockQuote>> = flow {
        try {
            val response = apiService.getStockQuote(authToken, symbol)
            
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Error fetching quote: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
