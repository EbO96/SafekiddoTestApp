package com.safekiddo.testapp.data

import com.safekiddo.testapp.data.db.dao.NewsDao
import com.safekiddo.testapp.data.db.entity.News
import com.safekiddo.testapp.data.mapper.NewsModelMapper
import com.safekiddo.testapp.data.rest.service.NewsRestService
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class NewsRepository(private val newsRestService: NewsRestService, private val newsDao: NewsDao) {

    fun getAllNews(): Single<List<News>> {
        return newsRestService.getAllNews()
                .toObservable()
                .flatMapIterable { it }
                .map(NewsModelMapper::map)
                .toList()
                .onErrorResumeNext { getCachedNews() }
                .flatMap { news ->
                    if (news.isEmpty()) {
                        getCachedNews()
                    } else {
                        Single.just(news)
                    }
                }
                .subscribeOn(Schedulers.io())
                .doOnSuccess(newsDao::insert)
    }

    private fun getCachedNews(): Single<List<News>> {
        return newsDao.getAllRx().subscribeOn(Schedulers.io())
    }

    fun createNews(news: News): Completable {
        return newsDao.insertRx(news).subscribeOn(Schedulers.io())
    }

    fun updateNews(news: News): Completable {
        return newsDao.updateRx(news).subscribeOn(Schedulers.io())
    }
}