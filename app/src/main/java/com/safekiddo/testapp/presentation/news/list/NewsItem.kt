package com.safekiddo.testapp.presentation.news.list

data class NewsItem(
        val newsId: Long,
        val imageUrl: String?,
        val title: String,
        val description: String
)