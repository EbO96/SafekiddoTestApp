package com.safekiddo.testapp.data.rest.service

import com.safekiddo.testapp.data.rest.model.NewsListApiResponse
import io.reactivex.Single
import retrofit2.http.GET

interface NewsRestService {

    @GET("posts")
    fun getAllNews(): Single<NewsListApiResponse>

    companion object {
        const val BASE_URL = "https://safekiddo.free.beeceptor.com/DBForCandidates/"
        const val MAX_CACHE_AGE_MS = 1000 * 60 * 60 * 12 // 12h
    }
}