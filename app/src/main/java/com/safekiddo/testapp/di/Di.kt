package com.safekiddo.testapp.di

import android.app.Application

object Di {

    /**
     * Core component. Should be initialized in [Application] using [init] method
     */
    lateinit var applicationComponent: ApplicationComponent
        private set

    fun init(application: Application) {
        applicationComponent = DaggerApplicationComponent
                .factory()
                .create(application)
    }
}