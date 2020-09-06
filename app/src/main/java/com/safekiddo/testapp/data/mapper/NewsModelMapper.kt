package com.safekiddo.testapp.data.mapper

import com.safekiddo.testapp.data.db.entity.News
import com.safekiddo.testapp.data.rest.model.NewsApiResponse

object NewsModelMapper : ModelMapper<NewsApiResponse, News>() {

    override fun map(input: NewsApiResponse): News {
        return News.Factory.create(input)
    }
}