package com.codingwithmitch.openapi.presentation.util

import androidx.recyclerview.widget.RecyclerView
import android.graphics.Rect
import android.view.View

class TopSpacingItemDecoration(private val padding: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = padding
    }
}