package com.safekiddo.testapp.presentation.news.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.safekiddo.testapp.data.NewsRepository
import com.safekiddo.testapp.presentation.BaseViewModel
import javax.inject.Inject

class NewsListViewModel(private val newsRepository: NewsRepository) : BaseViewModel() {

    val newsList = newsRepository.observeAll()

    init {
        newsRepository.getAllNews()
                .ignoreElement()
                .onErrorComplete {
                    return@onErrorComplete true
                }
                .subscribe()
                .addToDisposables()
    }

    class Factory @Inject constructor(private val newsRepository: NewsRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return NewsListViewModel(newsRepository) as T
        }
    }
}