package com.example.niftylive

import android.app.Application
import com.example.niftylive.di.ServiceLocator

class NiftyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.initialize(applicationContext)
    }
}
