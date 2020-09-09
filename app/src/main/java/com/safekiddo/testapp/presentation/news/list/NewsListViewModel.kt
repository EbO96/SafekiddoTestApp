package com.safekiddo.testapp.presentation.news.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hadilq.liveevent.LiveEvent
import com.safekiddo.testapp.data.NewsRepository
import com.safekiddo.testapp.data.mapper.DatabaseNewsToNewsItemMapper
import com.safekiddo.testapp.data.rest.LoadState
import com.safekiddo.testapp.presentation.BaseViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class NewsListViewModel(private val newsRepository: NewsRepository) : BaseViewModel() {

    private val _newsList = MutableLiveData<List<NewsItem>>(emptyList())
    val newsList: MutableLiveData<List<NewsItem>>
        get() = _newsList

    private val _isLoading = MutableLiveData<Boolean>()

    val isLoading: LiveData<Boolean>
        get() = _isLoading
    private val _error = LiveEvent<LoadState.Error.ErrorType>()

    val error: LiveData<LoadState.Error.ErrorType>
        get() = _error

    private val refreshPublishSubject = BehaviorSubject.create<Boolean>()

    init {
        load()
        refreshPublishSubject
                .toFlowable(BackpressureStrategy.LATEST)
                .toObservable()
                .flatMap(newsRepository::observeAllNews)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _isLoading.value = true }
                .doOnEach {
                    _isLoading.value = !(it.isOnComplete || it.isOnError || it.isOnNext)
                }
                .doOnNext {
                    when (it) {
                        is LoadState.Success -> _newsList.postValue(it.data.map(DatabaseNewsToNewsItemMapper::map))
                        is LoadState.Error -> _error.postValue(it.type)
                    }
                }
                .subscribe()
                .addToDisposables()
    }

    fun load(refresh: Boolean = true) {
        refreshPublishSubject.onNext(refresh)
    }

    class Factory @Inject constructor(private val newsRepository: NewsRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return NewsListViewModel(newsRepository) as T
        }
    }
}