package com.safekiddo.testapp.di.data.db

import android.content.Context
import com.safekiddo.testapp.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): AppDatabase = AppDatabase.build(context)
}