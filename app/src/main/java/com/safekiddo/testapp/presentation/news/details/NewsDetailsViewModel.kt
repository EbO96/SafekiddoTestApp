package com.safekiddo.testapp.presentation.news.details

import androidx.lifecycle.*
import com.hadilq.liveevent.LiveEvent
import com.safekiddo.testapp.data.NewsRepository
import com.safekiddo.testapp.data.db.entity.News
import com.safekiddo.testapp.data.mapper.DatabaseNewsToNewsItemMapper
import com.safekiddo.testapp.data.model.ImageSource
import com.safekiddo.testapp.presentation.BaseViewModel
import com.safekiddo.testapp.presentation.news.list.NewsItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class NewsDetailsViewModel(originalNews: NewsItem?, private val newsRepository: NewsRepository) : BaseViewModel() {

    private val createNews: Boolean = originalNews == null

    private val _viewMode = MutableLiveData(getInitialMode(originalNews))
    val viewMode: LiveData<ViewMode>
        get() = _viewMode

    val isInEditMode: Boolean get() = _viewMode.value is ViewMode.Edit

    private val _titleCharactersCount = MediatorLiveData<Int>()
    val titleCharactersCount: LiveData<Int>
        get() = _titleCharactersCount

    private val _event = LiveEvent<Event>()
    val event: LiveEvent<Event>
        get() = _event

    private val news: NewsItem? get() = _viewMode.value?.newsDetails

    init {
        _titleCharactersCount.addSource(_viewMode) {
            _titleCharactersCount.value = it.newsDetails?.title.charactersCount()
        }
    }

    private fun getInitialMode(originalNews: NewsItem?): ViewMode {
        return if (createNews) {
            ViewMode.Edit(null)
        } else {
            ViewMode.Preview(originalNews)
        }
    }

    fun updateTitleCharactersCount(title: String?) {
        _titleCharactersCount.value = title.charactersCount()
    }

    private fun String?.charactersCount() = this?.length ?: 0

    fun editNews() {
        _viewMode.value = ViewMode.Edit(news)
    }

    fun saveNews(imageSource: ImageSource?, title: String, description: String) {
        val databaseNews = News.Factory.create(
                id = news?.newsId,
                title = title,
                description = description,
                imageSource = imageSource,
                modificationDate = System.currentTimeMillis()
        )

        newsRepository.insertRx(databaseNews)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _viewMode.value = ViewMode.Preview(DatabaseNewsToNewsItemMapper.map(databaseNews))
                    _event.value = Event.NewsSaved
                }, {
                    _event.value = Event.Error
                })
                .addToDisposables()
    }

    fun deleteNews() {
        newsRepository.deleteById(news?.newsId ?: return)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _event.value = Event.NewsDeleted
                }, {
                    _event.value = Event.Error
                })
                .addToDisposables()
    }

    sealed class ViewMode(val newsDetails: NewsItem?) {
        class Edit(news: NewsItem?) : ViewMode(news)
        class Preview(news: NewsItem?) : ViewMode(news)
    }

    sealed class Event {
        object NewsDeleted : Event()
        object NewsSaved : Event()
        object Error : Event()
    }

    class Factory @Inject constructor(private val news: NewsItem?, private val newsRepository: NewsRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return NewsDetailsViewModel(news, newsRepository) as T
        }
    }
}