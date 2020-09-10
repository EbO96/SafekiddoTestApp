package com.safekiddo.testapp.data.mapper

import com.safekiddo.testapp.data.db.entity.News
import com.safekiddo.testapp.presentation.news.list.NewsItem

object DatabaseNewsToNewsItemMapper : ModelMapper<News, NewsItem>() {

    override fun map(input: News): NewsItem {
        return NewsItem(
                newsId = input.id,
                imageSource = input.imageSource,
                title = input.title,
                description = input.description
        )
    }
}