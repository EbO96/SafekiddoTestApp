package com.safekiddo.testapp.data.mapper

import com.safekiddo.testapp.data.db.entity.News
import com.safekiddo.testapp.data.rest.model.NewsApiResponse

class ApiResponseNewsToDatabaseNewsMapper(private val currentTime: Long) : ModelMapper<NewsApiResponse, News>() {

    override fun map(input: NewsApiResponse): News {
        return News.Factory.create(input, currentTime)
    }
}