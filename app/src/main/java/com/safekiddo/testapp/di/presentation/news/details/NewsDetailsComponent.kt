package com.safekiddo.testapp.di.presentation.news.details

import com.safekiddo.testapp.presentation.news.details.NewsDetailsFragment
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
        fun create(@BindsInstance newsId: Long, @BindsInstance newsDetailsFragment: NewsDetailsFragment): NewsDetailsComponent
    }
}