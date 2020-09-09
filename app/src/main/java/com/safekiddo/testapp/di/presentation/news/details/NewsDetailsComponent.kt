package com.safekiddo.testapp.di.presentation.news.details

import com.safekiddo.testapp.presentation.news.details.NewsDetailsFragment
import com.safekiddo.testapp.presentation.news.list.NewsItem
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(
        modules = [
            NewsDetailsModule::class
        ]
)
interface NewsDetailsComponent {

    fun inject(newsDetailsFragment: NewsDetailsFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance news: NewsItem?, @BindsInstance newsDetailsFragment: NewsDetailsFragment): NewsDetailsComponent
    }
}