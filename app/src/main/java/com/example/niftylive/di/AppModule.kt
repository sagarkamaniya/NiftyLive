package com.example.niftylive.di

import android.content.Context
import com.example.niftylive.utils.SecurePrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // This teaches Hilt how to build your SecurePrefs class
    @Provides
    @Singleton
    fun provideSecurePrefs(@ApplicationContext context: Context): SecurePrefs {
        // It needs the application context, so Hilt provides it
        return SecurePrefs(context)
    }
}
