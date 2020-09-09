package com.safekiddo.testapp.functional.util

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import com.safekiddo.testapp.R

fun EditText.textOrBlank() = text?.toString() ?: ""

fun EditText.setReadOnly(value: Boolean) {
    isFocusable = !value
    isFocusableInTouchMode = !value
    isActivated = !value
    minHeight = if (value) 0 else context.resources.getDimensionPixelSize(R.dimen.min_edit_text_height)
}

fun Context.shortToast(@StringRes message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}