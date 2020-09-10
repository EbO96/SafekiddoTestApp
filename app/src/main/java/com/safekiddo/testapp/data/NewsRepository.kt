package com.safekiddo.testapp.data

import com.safekiddo.testapp.data.db.dao.NewsDao
import com.safekiddo.testapp.data.rest.service.NewsRestService

class NewsRepository(private val newsRestService: NewsRestService, private val newsDao: NewsDao) : NewsDao by newsDao, NewsRestService by newsRestService