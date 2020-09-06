package com.safekiddo.testapp.di.presentation.news

import androidx.lifecycle.ViewModelProvider
import com.safekiddo.testapp.presentation.news.list.NewsListFragment
import com.safekiddo.testapp.presentation.news.list.NewsListViewModel
import dagger.Module
import dagger.Provides

@Module
class NewsListModule {

    @Provides
    fun provideViewModel(owner: NewsListFragment, factory: NewsListViewModel.Factory): NewsListViewModel {
        return ViewModelProvider(owner, factory)[NewsListViewModel::class.java]
    }
}