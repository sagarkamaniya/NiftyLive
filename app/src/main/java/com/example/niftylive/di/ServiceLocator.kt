package com.example.niftylive.di

import android.content.Context
import com.example.niftylive.data.api.RetrofitProvider
import com.example.niftylive.data.repository.NiftyRepository
import com.example.niftylive.utils.SecurePrefs

object ServiceLocator {
    private lateinit var appContext: Context
    lateinit var prefs: SecurePrefs
    lateinit var retrofitProvider: RetrofitProvider
    lateinit var niftyRepository: NiftyRepository

    fun initialize(context: Context) {
        appContext = context.applicationContext
        prefs = SecurePrefs(appContext)
        retrofitProvider = RetrofitProvider()
        niftyRepository = NiftyRepository(retrofitProvider.service, prefs)
    }
}