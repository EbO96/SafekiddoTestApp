package com.safekiddo.testapp.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class ImageSource(val path: String, val type: Type) : Parcelable {

    fun getImage(): Any = when (type) {
        Type.NETWORK -> path
        Type.FILE -> File(path)
        Type.URI -> Uri.parse(path)
    }

    enum class Type {
        NETWORK,
        FILE,
        URI
    }

    object Factory {

        fun create(file: File?): ImageSource? {
            return file?.absolutePath?.let { ImageSource(it, Type.FILE) }
        }

        fun create(uri: Uri?): ImageSource? {
            return uri?.toString()?.let { ImageSource(it, Type.URI) }
        }

        fun create(imageUrl: String?): ImageSource? {
            return imageUrl?.let { ImageSource(it, Type.NETWORK) }
        }
    }
}