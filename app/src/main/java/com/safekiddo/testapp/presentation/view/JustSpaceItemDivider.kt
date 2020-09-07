package com.safekiddo.testapp.presentation.view

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

class JustSpaceItemDivider(context: Context, @DimenRes padding: Int) : RecyclerView.ItemDecoration() {

    private val padding: Int = context.resources.getDimension(padding).toInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.bottom = padding
    }
}