package dev.psuchanek.jonsfueltracker_v_1_1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.TripHistoryListItemBinding
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.ui.history.OnTripClickListener

class TripHistoryAdapter(private val onTripClickListener: OnTripClickListener) :
    ListAdapter<FuelTrackerTrip, TripHistoryAdapter.ViewHolder>(
        TripsDiffCall
    ) {

    class ViewHolder(val binding: TripHistoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            trip: FuelTrackerTrip,
            onTripClickListener: OnTripClickListener
        ) {
            binding.trip = trip
            binding.ivArrow.setOnClickListener {
                onTripClickListener.onClick(trip, adapterPosition)
            }

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
                return ViewHolder(
                    binding
                )
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

    override fun getItemCount() = currentList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trip = getItem(position)
        holder.bind(trip, onTripClickListener)

    }


}