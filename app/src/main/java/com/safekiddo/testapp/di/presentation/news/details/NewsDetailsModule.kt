package com.safekiddo.testapp.di.presentation.news.details

import androidx.lifecycle.ViewModelProvider
import com.safekiddo.testapp.presentation.news.details.NewsDetailsFragment
import com.safekiddo.testapp.presentation.news.details.NewsDetailsViewModel
import dagger.Module
import dagger.Provides

@Module
class NewsDetailsModule {

    @Provides
    fun provideViewModel(owner: NewsDetailsFragment, factory: NewsDetailsViewModel.Factory): NewsDetailsViewModel {
        return ViewModelProvider(owner, factory)[NewsDetailsViewModel::class.java]
    }
}