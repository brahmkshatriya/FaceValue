package dev.brahmkshatriya.facevalue.utils

import android.content.Context
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

object UiUtils {

    fun SwipeRefreshLayout.configure(block: () -> Unit) {
        setProgressViewOffset(true, 0, 64.dpToPx(context))
        setOnRefreshListener(block)
    }

    fun Int.dpToPx(context: Context) = (this * context.resources.displayMetrics.density).toInt()
}