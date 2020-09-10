package com.safekiddo.testapp.presentation.news.details

import androidx.lifecycle.*
import com.hadilq.liveevent.LiveEvent
import com.safekiddo.testapp.data.NewsRepository
import com.safekiddo.testapp.data.db.entity.News
import com.safekiddo.testapp.data.model.ImageSource
import com.safekiddo.testapp.presentation.BaseViewModel
import com.safekiddo.testapp.presentation.news.list.NewsItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class NewsDetailsViewModel(originalNews: NewsItem?, private val newsRepository: NewsRepository, private val savedStateHandle: SavedStateHandle) : BaseViewModel() {

    private val _newsId = savedStateHandle.getLiveData<Long?>(ARG_NEWS_ID)
    val newsId: Long? get() = _newsId.value

    private val _imageSource = savedStateHandle.getLiveData<ImageSource?>(ARG_PHOTO_SOURCE)
    val imageSource: LiveData<ImageSource?>
        get() = _imageSource

    private val _title = savedStateHandle.getLiveData<String?>(ARG_TITLE)
    val title: LiveData<String?>
        get() = _title

    private val _description = savedStateHandle.getLiveData<String?>(ARG_DESCRIPTION)
    val description: LiveData<String?>
        get() = _description

    private val _titleCharactersCount = MutableLiveData<Int>()
    val titleCharactersCount: LiveData<Int>
        get() = _titleCharactersCount

    private val _uiMode = savedStateHandle.getLiveData<UiMode>(ARG_UI_MODE)
    val uiMode: LiveData<UiMode>
        get() = _uiMode

    private val _event = LiveEvent<Event>()
    val event: LiveEvent<Event>
        get() = _event

    val isInEditMode: Boolean get() = _uiMode.value == UiMode.EDIT


    init {
        if (savedStateHandle.keys().isEmpty()) {
            updateNewsId(originalNews?.newsId)
            updateImageSource(originalNews?.imageSource)
            saveUiState(originalNews?.title, originalNews?.description)
            changeUiMode(getInitialUiMode())
        }
    }

    private fun updateNewsId(id: Long?) {
        savedStateHandle.set(ARG_NEWS_ID, id)
    }

    fun updateImageSource(imageSource: ImageSource?) {
        savedStateHandle.set(ARG_PHOTO_SOURCE, imageSource)
    }

    fun saveUiState(title: String?, description: String?) {
        savedStateHandle.apply {
            set(ARG_TITLE, title)
            set(ARG_DESCRIPTION, description)
        }
    }

    private fun getInitialUiMode(): UiMode {
        return if (newsId == null) {
            UiMode.EDIT
        } else {
            UiMode.PREVIEW
        }
    }

    fun updateTitleCharactersCount(title: String?) {
        _titleCharactersCount.value = title.charactersCount()
    }

    private fun String?.charactersCount() = this?.length ?: 0

    fun editNews() {
        changeUiMode(UiMode.EDIT)
    }

    fun saveNews(title: String, description: String) {
        val databaseNews = News.Factory.create(
                id = newsId,
                title = title,
                description = description,
                imageSource = imageSource.value,
                modificationDate = System.currentTimeMillis()
        )

        newsRepository.insertRx(databaseNews)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate {
                    _title.value = title
                    _description.value = description
                }
                .subscribe({
                    updateNewsId(databaseNews.id)
                    changeUiMode(UiMode.PREVIEW)
                    _event.value = Event.NewsSaved
                }, {
                    _event.value = Event.Error
                })
                .addToDisposables()
    }

    private fun changeUiMode(uiMode: UiMode) {
        savedStateHandle.set(ARG_UI_MODE, uiMode)
    }

    fun deleteNews() {
        newsRepository.deleteById(newsId ?: return)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _event.value = Event.NewsDeleted
                }, {
                    _event.value = Event.Error
                })
                .addToDisposables()
    }

    companion object {
        private const val ARG_UI_MODE = "ARG_UI_MODE"
        private const val ARG_PHOTO_SOURCE = "ARG_PHOTO_SOURCE"
        private const val ARG_TITLE = "ARG_TITLE"
        private const val ARG_DESCRIPTION = "ARG_DESCRIPTION"
        private const val ARG_NEWS_ID = "ARG_NEWS_ID"
    }

    enum class UiMode {
        EDIT,
        PREVIEW
    }

    sealed class Event {
        object NewsDeleted : Event()
        object NewsSaved : Event()
        object Error : Event()
    }

    class Factory @Inject constructor(private val news: NewsItem?, private val newsRepository: NewsRepository, owner: NewsDetailsFragment) : AbstractSavedStateViewModelFactory(owner, null) {

        override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            @Suppress("UNCHECKED_CAST")
            return NewsDetailsViewModel(news, newsRepository, handle) as T
        }
    }
}