package com.safekiddo.testapp.presentation.news.details

import androidx.lifecycle.*
import com.safekiddo.testapp.data.NewsRepository
import com.safekiddo.testapp.data.db.entity.News
import com.safekiddo.testapp.presentation.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class NewsDetailsViewModel(newsId: Long, private val newsRepository: NewsRepository) : BaseViewModel() {

    private val _news = MutableLiveData<NewsDetails?>()
    val news: LiveData<NewsDetails?>
        get() = _news

    init {
        newsRepository.getByIdRx(newsId)
                .subscribeOn(Schedulers.io())
                .map { NewsDetails(news = it, titleCharactersCount = it.title.length) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _news.value = it
                }, {
                    _news.value = null
                })
                .addToDisposables()
    }

    data class NewsDetails(val news: News, val titleCharactersCount: Int)

    class Factory @Inject constructor(private val newsId: Long, private val newsRepository: NewsRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return NewsDetailsViewModel(newsId, newsRepository) as T
        }
    }
}