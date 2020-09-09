package com.safekiddo.testapp.presentation.news.details

import androidx.lifecycle.*
import com.safekiddo.testapp.data.NewsRepository
import com.safekiddo.testapp.data.db.entity.News
import com.safekiddo.testapp.presentation.BaseViewModel
import com.safekiddo.testapp.presentation.news.list.NewsItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class NewsDetailsViewModel(private val originalNews: NewsItem?, private val newsRepository: NewsRepository) : BaseViewModel() {

    private val createNews: Boolean = originalNews == null

    private val _viewMode = MutableLiveData(getInitialMode())
    val viewMode: LiveData<ViewMode>
        get() = _viewMode

    val isInEditMode: Boolean get() = _viewMode.value is ViewMode.Edit

    private val _titleCharactersCount = MediatorLiveData<Int>()
    val titleCharactersCount: LiveData<Int>
        get() = _titleCharactersCount

    private val newsDetails: NewsDetails? get() = _viewMode.value?.newsDetails

    init {
        _titleCharactersCount.addSource(_viewMode) {
            _titleCharactersCount.value = it.newsDetails?.title.charactersCount()
        }
    }

    private fun getInitialMode(): ViewMode {
        val news = NewsDetails.Factory.create(originalNews)
        return if (createNews) {
            ViewMode.Edit(news)
        } else {
            ViewMode.Preview(news)
        }
    }

    fun updateTitleCharactersCount(title: String?) {
        _titleCharactersCount.value = title.charactersCount()
    }

    private fun String?.charactersCount() = this?.length ?: 0

    fun editNews() {
        _viewMode.value = ViewMode.Edit(newsDetails)
    }

    fun saveNews(imagePath: String? = originalNews?.imageUrl, title: String, description: String) {
        // TODO image source class
        val news = NewsDetails.Factory.create(originalNews?.newsId, imagePath, title, description)
        val databaseNews = News.Factory.create(
                title = title,
                description = description,
                id = originalNews?.newsId,
                imageUrl = imagePath,
                modificationDate = System.currentTimeMillis()
        )

        newsRepository.insertRx(databaseNews)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _viewMode.value = ViewMode.Preview(news)
                }, {

                })
                .addToDisposables()
    }

    fun deleteNews() {
        newsRepository.deleteById(originalNews?.newsId ?: return)
                .subscribeOn(Schedulers.io())
                .subscribe()
                .addToDisposables()
    }

    sealed class ViewMode(val newsDetails: NewsDetails?) {
        class Edit(news: NewsDetails?) : ViewMode(news)
        class Preview(news: NewsDetails?) : ViewMode(news)
    }

    data class NewsDetails(val newsId: Long?, val image: String, val title: String, val description: String) {

        object Factory {
            fun create(id: Long?, image: String?, title: String?, description: String?): NewsDetails {
                return NewsDetails(
                        newsId = id,
                        image = image ?: "",
                        title = title ?: "",
                        description = description ?: ""
                )
            }

            fun create(news: NewsItem?): NewsDetails {
                return create(
                        id = news?.newsId,
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