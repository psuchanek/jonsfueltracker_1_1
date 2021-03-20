package dev.psuchanek.jonsfueltracker_v_1_1.adapters.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.TripHistoryListItemBinding
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.ui.history.HistoryViewModel

class TripHistoryViewHolder(val binding: TripHistoryListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        trip: FuelTrackerTrip,
        viewModel: HistoryViewModel? = null
    ) {
        viewModel?.let {
            binding.viewModel = viewModel
        }
        binding.trip = trip
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): TripHistoryViewHolder {
            val binding = DataBindingUtil.inflate<TripHistoryListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.trip_history_list_item,
                parent,
                false
            )
            return TripHistoryViewHolder(
                binding
            )
        }
    }
}