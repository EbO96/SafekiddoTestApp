package com.safekiddo.testapp.data.network.model

import com.google.gson.annotations.SerializedName

/**
 * Model used by networking layer. It should be mapped to [Post] entity object.
 */
data class PostApiResponse(
        @SerializedName("id")
        val id: Int?,

        @SerializedName("title")
        val title: String?,

        @SerializedName("description")
        val description: String?,

        @SerializedName("image_url")
        val imageUrl: String?
)