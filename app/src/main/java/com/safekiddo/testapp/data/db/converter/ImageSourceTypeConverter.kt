package com.safekiddo.testapp.data.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.safekiddo.testapp.data.model.ImageSource

class ImageSourceTypeConverter {

    @TypeConverter
    fun fromJson(json: String?): ImageSource? {
        return Gson().fromJson(json, ImageSource::class.java)
    }

    @TypeConverter
    fun toJson(imageSource: ImageSource?): String? {
        return Gson().toJson(imageSource)
    }
}