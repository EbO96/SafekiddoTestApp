package com.safekiddo.testapp.data.mapper

import com.safekiddo.testapp.data.db.entity.News
import com.safekiddo.testapp.data.rest.model.NewsApiResponse

object ApiResponseNewsToDatabaseNewsMapper : ModelMapper<NewsApiResponse, News>() {

    fun map(input: NewsApiResponse, modificationDate: Long): News {
        return map(input).copy(modificationDate = modificationDate)
    }

    override fun map(input: NewsApiResponse): News {
        return News.Factory.create(input, -1)
    }
}