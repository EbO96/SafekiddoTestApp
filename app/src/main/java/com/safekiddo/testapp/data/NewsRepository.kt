package com.safekiddo.testapp.data

import com.safekiddo.testapp.data.db.dao.NewsDao
import com.safekiddo.testapp.data.db.entity.News
import com.safekiddo.testapp.data.mapper.ApiResponseNewsToDatabaseNewsMapper
import com.safekiddo.testapp.data.rest.LoadState
import com.safekiddo.testapp.data.rest.model.NewsListApiResponse
import com.safekiddo.testapp.data.rest.service.NewsRestService
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class NewsRepository(private val newsRestService: NewsRestService, private val newsDao: NewsDao) : BaseRepository(), NewsDao by newsDao {

    fun observeAllNews(refresh: Boolean): Observable<LoadState<List<News>>> {
        return getCachedNews()
                .flatMap {
                    if (refresh || it.isEmpty()) {
                        getApiNews()
                    } else {
                        Observable.just(it)
                    }
                }
                .asLoadState()
                .subscribeOn(Schedulers.io())
    }

    private fun getCachedNews(): Observable<List<News>> {
        return newsDao.observeAllRx()
    }

    private fun getApiNews(): Observable<List<News>> {
        val currentTime = System.currentTimeMillis()
        return newsRestService.getAllNews()
                .map(NewsListApiResponse::news)
                .flatMapIterable { it }
                .map { ApiResponseNewsToDatabaseNewsMapper.map(it, currentTime) }
                .toList()
                .doOnSuccess(::cacheNews)
                .toObservable()
    }

    private fun cacheNews(news: List<News>?) {
        news?.run(newsDao::insert)
    }

    fun createNews(news: News): Completable {
        return newsDao.insertRx(news).subscribeOn(Schedulers.io())
    }

    fun updateNews(news: News): Completable {
        return newsDao.updateRx(news).subscribeOn(Schedulers.io())
    }
}