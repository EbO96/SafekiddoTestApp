package com.safekiddo.testapp.di.presentation.news

import com.safekiddo.testapp.presentation.news.list.NewsListFragment
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(
        modules = [
            NewsListModule::class
        ]
)
interface NewsListComponent {

    fun inject(newsListFragment: NewsListFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance newsListFragment: NewsListFragment): NewsListComponent
    }
}