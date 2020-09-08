package com.safekiddo.testapp.data

import com.safekiddo.testapp.data.db.dao.NewsDao
import com.safekiddo.testapp.data.db.entity.News
import com.safekiddo.testapp.data.mapper.ApiResponseNewsToDatabaseNewsMapper
import com.safekiddo.testapp.data.rest.RestApiResponse
import com.safekiddo.testapp.data.rest.model.NewsListApiResponse
import com.safekiddo.testapp.data.rest.service.NewsRestService
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class NewsRepository(private val newsRestService: NewsRestService, private val newsDao: NewsDao) : BaseRepository(), NewsDao by newsDao {

    fun getAllNews(): Single<RestApiResponse<List<News>>> {
        return getCachedNews()
                .flatMap {
                    if (it.isEmpty()) {
                        getApiNews()
                    } else {
                        Single.just(it)
                    }
                }
                .asRestApiResponse()
                .subscribeOn(Schedulers.io())
    }

    private fun getCachedNews(): Single<List<News>> {
        return newsDao.getAllRx()
    }

    private fun getApiNews(): Single<List<News>> {
        return newsRestService.getAllNews()
                .toObservable()
                .map(NewsListApiResponse::news)
                .flatMapIterable { it }
                .map(ApiResponseNewsToDatabaseNewsMapper::map)
                .toList()
                .doOnSuccess(::cacheNews)
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