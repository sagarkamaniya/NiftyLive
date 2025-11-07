package com.example.niftylive.di

import android.content.Context
import com.example.niftylive.data.api.RetrofitProvider
import com.example.niftylive.data.repository.NiftyRepository
import com.example.niftylive.utils.SecurePrefs

object ServiceLocator {
    private lateinit var appContext: Context
    lateinit var prefs: SecurePrefs
    // expose the retrofit provider singleton (no constructor)
    // if you prefer to keep the previous names, you can also rename as needed.
    // Use the 'api' property declared in RetrofitProvider (val api: SmartApiService)
    lateinit var niftyRepository: NiftyRepository

    fun initialize(context: Context) {
        appContext = context.applicationContext
        prefs = SecurePrefs(appContext)

        // RetrofitProvider is an object singleton; access its 'api' property
        val smartApi = RetrofitProvider.api

        // create repository with the SmartApiService and prefs
        niftyRepository = NiftyRepository(smartApi, prefs)
    }
}