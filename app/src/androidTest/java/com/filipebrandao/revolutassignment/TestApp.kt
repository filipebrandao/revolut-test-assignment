package com.filipebrandao.revolutassignment

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Clean slate app that doesn't load immediately the koin modules - those are loaded in the tests
 */
class TestApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            modules(emptyList())
        }
    }
}
