package com.safekiddo.testapp.data.rest.service

import com.safekiddo.testapp.data.rest.model.NewsApiResponse
import io.reactivex.Single
import retrofit2.http.GET

interface NewsRestService {

    @GET("posts")
    fun getAllNews(): Single<List<NewsApiResponse>>

    companion object {
        const val BASE_URL = "https://safekiddo.free.beeceptor.com/DBForCandidates/"
    }
}