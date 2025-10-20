package com.kreggscode.waisttohip

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CurveAndFuelApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any SDKs or libraries here
    }
}
