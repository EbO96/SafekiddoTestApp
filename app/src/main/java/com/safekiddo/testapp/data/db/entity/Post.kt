package com.safekiddo.testapp.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post")
data class Post(
        @PrimaryKey
        @ColumnInfo(name = "id")
        val id: Int,

        @ColumnInfo(name = "title")
        val title: String,

        @ColumnInfo(name = "description")
        val description: String,

        @ColumnInfo(name = "image_url")
        val imageUrl: String?
)