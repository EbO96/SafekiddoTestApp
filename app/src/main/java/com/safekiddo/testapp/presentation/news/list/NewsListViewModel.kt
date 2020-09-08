package com.safekiddo.testapp.presentation.news.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hadilq.liveevent.LiveEvent
import com.safekiddo.testapp.data.NewsRepository
import com.safekiddo.testapp.data.mapper.DatabaseNewsToNewsItemMapper
import com.safekiddo.testapp.data.rest.RestApiResponse
import com.safekiddo.testapp.presentation.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewsListViewModel(private val newsRepository: NewsRepository) : BaseViewModel() {

    private val _newsList = MutableLiveData<List<NewsItem>>(emptyList())
    val newsList: MutableLiveData<List<NewsItem>>
        get() = _newsList

    private val _isLoading = MutableLiveData<Boolean>()

    val isLoading: LiveData<Boolean>
        get() = _isLoading
    private val _error = LiveEvent<RestApiResponse.Error.ErrorType>()

    val error: LiveData<RestApiResponse.Error.ErrorType>
        get() = _error

    init {
        refresh()
    }

    fun refresh() {
        newsRepository.getNews()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _isLoading.value = true }
                .doAfterTerminate { _isLoading.value = false }
                .observeOn(Schedulers.computation())
                .doOnSuccess {
                    when (it) {
                        is RestApiResponse.Success -> _newsList.postValue(it.data.map(DatabaseNewsToNewsItemMapper::map))
                        is RestApiResponse.Error -> _error.postValue(it.type)
                    }
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