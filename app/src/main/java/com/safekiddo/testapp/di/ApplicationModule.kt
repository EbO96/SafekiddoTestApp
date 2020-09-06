package com.safekiddo.testapp.di

import android.app.Application
import android.content.Context
import com.safekiddo.testapp.di.presentation.news.NewsListComponent
import dagger.Module
import dagger.Provides

@Module(
        subcomponents = [
            NewsListComponent::class
        ]
)
class ApplicationModule {

    @Provides
    fun provideContext(application: Application): Context = application.applicationContext
}