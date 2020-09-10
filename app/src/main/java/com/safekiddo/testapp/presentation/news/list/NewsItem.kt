package com.safekiddo.testapp.presentation.news.list

import android.os.Parcelable
import com.safekiddo.testapp.data.model.ImageSource
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewsItem(
        val newsId: Long,
        val imageSource: ImageSource?,
        val title: String,
        val description: String
) : Parcelable