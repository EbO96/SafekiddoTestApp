package com.safekiddo.testapp.data.rest.model

import com.google.gson.annotations.SerializedName

/**
 * Model used by networking layer. It should be mapped to [News] entity object.
 */
data class NewsApiResponse(
        @SerializedName("id")
        val id: Long?,

        @SerializedName("title")
        val title: String?,

        @SerializedName("description")
        val description: String?,

        @SerializedName("icon")
        val imageUrl: String?
)