package com.safekiddo.testapp.functional.util

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes

fun EditText.textOrBlank() = text?.toString() ?: ""

fun Context.shortToast(@StringRes message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}