package com.safekiddo.testapp.data.rest.model

import com.google.gson.annotations.SerializedName

data class NewsListApiResponse(
        @SerializedName("posts")
        val news: List<NewsApiResponse>
)