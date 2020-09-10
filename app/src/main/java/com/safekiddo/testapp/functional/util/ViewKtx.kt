package com.safekiddo.testapp.functional.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment


fun EditText.textOrBlank() = text?.toString() ?: ""

fun Context.shortToast(@StringRes message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.hideKeyboard() {
    val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
    val token = view?.rootView?.windowToken ?: return
    imm?.hideSoftInputFromWindow(token, 0)
}