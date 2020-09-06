package com.safekiddo.testapp

import android.app.Application
import com.safekiddo.testapp.di.Di

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Di.init(this)
    }
}