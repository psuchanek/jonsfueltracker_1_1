package dev.psuchanek.jonsfueltracker_v_1_1.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.TripHistoryListItemBinding
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip

class TripHistoryAdapter(private val onTripClickListener: OnTripClickListener) :
    ListAdapter<FuelTrackerTrip, TripHistoryAdapter.ViewHolder>(TripsDiffCall) {

    class ViewHolder(private val binding: TripHistoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: FuelTrackerTrip, onTripClickListener: OnTripClickListener) {
            binding.trip = trip
            binding.onClickListener = onTripClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = DataBindingUtil.inflate<TripHistoryListItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.trip_history_list_item,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }
    }

    object TripsDiffCall : DiffUtil.ItemCallback<FuelTrackerTrip>() {
        override fun areItemsTheSame(oldItem: FuelTrackerTrip, newItem: FuelTrackerTrip): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FuelTrackerTrip,
            newItem: FuelTrackerTrip
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TripHistoryAdapter.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TripHistoryAdapter.ViewHolder, position: Int) {
        val trip = getItem(position)
        holder.bind(trip, onTripClickListener)
    }
}