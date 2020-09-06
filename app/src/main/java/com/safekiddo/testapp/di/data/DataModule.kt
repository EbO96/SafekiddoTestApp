package com.safekiddo.testapp.di.data

import com.safekiddo.testapp.data.NewsRepository
import com.safekiddo.testapp.data.db.AppDatabase
import com.safekiddo.testapp.data.rest.service.NewsRestService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Singleton
    @Provides
    fun provideNewsRepository(newsRestService: NewsRestService, appDatabase: AppDatabase): NewsRepository {
        return NewsRepository(newsRestService, appDatabase.newsDao)
    }
}