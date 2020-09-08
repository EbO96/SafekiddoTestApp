package com.safekiddo.testapp.di

import android.app.Application
import android.content.Context
import com.safekiddo.testapp.di.presentation.news.details.NewsDetailsComponent
import com.safekiddo.testapp.di.presentation.news.list.NewsListComponent
import dagger.Module
import dagger.Provides

@Module(
        subcomponents = [
            NewsListComponent::class,
            NewsDetailsComponent::class
        ]
)
class ApplicationModule {

    @Provides
    fun provideContext(application: Application): Context = application.applicationContext
}