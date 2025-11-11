package com.example.niftylive.di

import com.example.niftylive.data.api.SmartApiService
// ... (other imports) ...
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor // <-- Make sure this is imported
import retrofit2.Retrofit
// ... (other imports) ...

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // ✅ STEP 1: UN-COMMENT THIS FUNCTION
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Use BODY to see everything
        }
    }

    @Provides
    @Singleton
    // ✅ STEP 2: ADD 'loggingInterceptor' BACK AS A PARAMETER
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient { 
        return OkHttpClient.Builder()
            // ✅ STEP 3: UN-COMMENT THIS LINE
            .addInterceptor(loggingInterceptor) 
            .build()
    }

    // ... (rest of the file is the same) ...
}
