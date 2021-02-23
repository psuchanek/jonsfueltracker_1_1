package dev.psuchanek.jonsfueltracker_v_1_1.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CustomItemDecoration(private val spacing: Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        parent.adapter?.let { adapter ->
            outRect.bottom = when(parent.getChildAdapterPosition(view)) {
                RecyclerView.NO_POSITION,
                adapter.itemCount -1 -> 0
                else -> spacing
            }
        }
    }
}