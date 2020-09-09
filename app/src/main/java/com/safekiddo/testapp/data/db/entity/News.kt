package com.safekiddo.testapp.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.safekiddo.testapp.data.rest.model.NewsApiResponse

@Entity(tableName = "news")
data class News(
        @PrimaryKey
        @ColumnInfo(name = "id")
        val id: Long = 0,

        @ColumnInfo(name = "title")
        val title: String,

        @ColumnInfo(name = "description")
        val description: String,

        @ColumnInfo(name = "image_url")
        val imageUrl: String?,

        @ColumnInfo(name = "modification_date")
        val modificationDate: Long
) {

    object Factory {

        private fun generateId() = System.nanoTime()

        fun create(id: Long?, title: String, description: String, imageUrl: String?, modificationDate: Long): News {
            return News(
                    id = id ?: generateId(),
                    title = title,
                    description = description,
                    imageUrl = imageUrl,
                    modificationDate = modificationDate
            )
        }

        fun create(newsApiResponse: NewsApiResponse, modificationDate: Long): News {
            return News(
                    id = newsApiResponse.id ?: generateId(),
                    title = newsApiResponse.title ?: "",
                    description = newsApiResponse.description ?: "",
                    imageUrl = newsApiResponse.imageUrl,
                    modificationDate = modificationDate
            )
        }
    }
}