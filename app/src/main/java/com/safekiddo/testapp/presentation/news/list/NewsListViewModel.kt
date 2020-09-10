package com.safekiddo.testapp.presentation.news.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hadilq.liveevent.LiveEvent
import com.safekiddo.testapp.data.NewsRepository
import com.safekiddo.testapp.data.db.entity.News
import com.safekiddo.testapp.data.mapper.ApiResponseNewsToDatabaseNewsMapper
import com.safekiddo.testapp.data.mapper.DatabaseNewsToNewsItemMapper
import com.safekiddo.testapp.data.rest.model.NewsListApiResponse
import com.safekiddo.testapp.presentation.BaseViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewsListViewModel(private val newsRepository: NewsRepository) : BaseViewModel() {

    private val _newsList = MutableLiveData<List<NewsItem>>(emptyList())
    val newsList: MutableLiveData<List<NewsItem>>
        get() = _newsList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _event = LiveEvent<Event>()
    val event: LiveData<Event>
        get() = _event

    init {
        // Observe local database
        newsRepository.observeAllRx()
                .subscribeOn(Schedulers.io())
                .map(DatabaseNewsToNewsItemMapper::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _newsList.value = it
                }, {
                    _newsList.value = emptyList()
                })
                .addToDisposables()

        refresh()
    }

    fun refresh() {
        // Fetch from Rest API
        newsRepository.getAllNews()
                .subscribeOn(Schedulers.io())
                .map {
                    val mapper = ApiResponseNewsToDatabaseNewsMapper(System.currentTimeMillis())
                    mapper.map(it.news)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _isLoading.value = true }
                .doAfterTerminate { _isLoading.value = false }
                .observeOn(Schedulers.io())
                .doOnSuccess(newsRepository::insert)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}) { _event.value = Event.Error }
                .addToDisposables()
    }

    sealed class Event {
        object Error : Event()
    }

    class Factory @Inject constructor(private val newsRepository: NewsRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return NewsListViewModel(newsRepository) as T
        }
    }
}