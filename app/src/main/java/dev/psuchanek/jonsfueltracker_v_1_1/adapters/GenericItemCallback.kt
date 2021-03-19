package dev.psuchanek.jonsfueltracker_v_1_1.adapters

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class GenericItemCallback<T : Any> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}
