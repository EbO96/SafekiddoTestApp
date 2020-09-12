package com.safekiddo.testapp.di

import android.app.Application
import com.safekiddo.testapp.di.data.DataSourceModule
import com.safekiddo.testapp.di.data.db.DatabaseModule
import com.safekiddo.testapp.di.data.rest.BaseRestModule
import com.safekiddo.testapp.di.data.rest.NewsRestModule
import com.safekiddo.testapp.di.presentation.news.details.NewsDetailsComponent
import com.safekiddo.testapp.di.presentation.news.list.NewsListComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            ApplicationModule::class,
            DataSourceModule::class,
            DatabaseModule::class,
            BaseRestModule::class,
            NewsRestModule::class
        ]
)
interface ApplicationComponent {

    fun getNewsListComponentFactory(): NewsListComponent.Factory

    fun getNewsDetailsComponentFactory(): NewsDetailsComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): ApplicationComponent
    }
}