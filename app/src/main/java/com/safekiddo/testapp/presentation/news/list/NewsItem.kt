package com.safekiddo.testapp.presentation.news.list

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewsItem(
        val newsId: Long,
        val imageUrl: String?,
        val title: String,
        val description: String
) : Parcelable