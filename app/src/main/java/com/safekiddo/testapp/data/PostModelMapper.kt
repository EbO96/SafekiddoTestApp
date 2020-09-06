package com.safekiddo.testapp.data

import com.safekiddo.testapp.data.db.entity.Post
import com.safekiddo.testapp.data.network.model.PostApiResponse

object PostModelMapper : ModelMapper<PostApiResponse, Post>() {

    override fun map(input: PostApiResponse): Post {
        return Post(
                id = input.id ?: System.nanoTime().toInt(),
                title = input.title ?: "",
                description = input.description ?: "",
                imageUrl = input.imageUrl
        )
    }
}