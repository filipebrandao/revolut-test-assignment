package com.filipebrandao.revolutassignment.utils

import androidx.recyclerview.widget.RecyclerView

/**
 * Scroll listener that is triggered when the RecyclerView stops scrolling
 */
abstract class OnScrollStoppedListener : RecyclerView.OnScrollListener() {

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            onScrollStopped(recyclerView)
        }
    }

    abstract fun onScrollStopped(recyclerView: RecyclerView)
}
