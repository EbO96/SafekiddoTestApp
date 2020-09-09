package com.safekiddo.testapp.presentation.news.details

import androidx.lifecycle.*
import com.hadilq.liveevent.LiveEvent
import com.safekiddo.testapp.data.NewsRepository
import com.safekiddo.testapp.data.db.entity.News
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

    private val news: NewsDetails? get() = _viewMode.value?.newsDetails

    init {
        _titleCharactersCount.addSource(_viewMode) {
            _titleCharactersCount.value = it.newsDetails?.title.charactersCount()
        }
    }

    private fun getInitialMode(originalNews: NewsItem?): ViewMode {
        return if (createNews) {
            ViewMode.Edit(null)
        } else {
            ViewMode.Preview(NewsDetails.Factory.create(originalNews))
        }
    }

    fun updateTitleCharactersCount(title: String?) {
        _titleCharactersCount.value = title.charactersCount()
    }

    private fun String?.charactersCount() = this?.length ?: 0

    fun editNews() {
        _viewMode.value = ViewMode.Edit(news)
    }

    fun saveNews(imagePath: String? = news?.image, title: String, description: String) {
        val news = NewsDetails.Factory.create(news?.newsId, imagePath, title, description)
        val databaseNews = News.Factory.create(
                id = news.newsId,
                title = news.title,
                description = news.description,
                imageUrl = news.image,
                modificationDate = System.currentTimeMillis()
        )

        newsRepository.insertRx(databaseNews)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _viewMode.value = ViewMode.Preview(news)
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

    sealed class ViewMode(val newsDetails: NewsDetails?) {
        class Edit(news: NewsDetails?) : ViewMode(news)
        class Preview(news: NewsDetails?) : ViewMode(news)
    }

    sealed class Event {
        object NewsDeleted : Event()
        object NewsSaved : Event()
        object Error : Event()
    }

    data class NewsDetails(val newsId: Long, val image: String, val title: String, val description: String) {

        object Factory {
            fun create(id: Long?, image: String?, title: String?, description: String?): NewsDetails {
                return NewsDetails(
                        newsId = id ?: News.Factory.generateId(),
                        image = image ?: "",
                        title = title ?: "",
                        description = description ?: ""
                )
            }

            fun create(news: NewsItem?): NewsDetails {
                return create(
                        id = news?.newsId ?: News.Factory.generateId(),
                        image = news?.imageUrl,
                        title = news?.title,
                        description = news?.description
                )
            }
        }
    }

    class Factory @Inject constructor(private val news: NewsItem?, private val newsRepository: NewsRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return NewsDetailsViewModel(news, newsRepository) as T
        }
    }
}