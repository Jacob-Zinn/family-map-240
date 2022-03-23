package com.nznlabs.familymap240

import android.app.Application
import com.nznlabs.familymap240.di.Modules.appModule
import com.nznlabs.familymap240.di.Modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        setupModules()
        setupTimber()
    }

    private fun setupModules() {
        startKoin {
            androidContext(applicationContext)
            modules(listOf(appModule, viewModelModule))
        }
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}