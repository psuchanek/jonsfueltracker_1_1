package dev.psuchanek.jonsfueltracker_v_1_1.adapters.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.VehicleListItemBinding
import dev.psuchanek.jonsfueltracker_v_1_1.models.Vehicle
import dev.psuchanek.jonsfueltracker_v_1_1.ui.MainViewModel

class VehicleViewHolder(val binding: VehicleListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(vehicle: Vehicle,
    viewModel: MainViewModel? = null) {
        binding.vehicle = vehicle
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): VehicleViewHolder {
            val binding = DataBindingUtil.inflate<VehicleListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.vehicle_list_item,
                parent,
                false
            )
            return VehicleViewHolder(
                binding
            )
        }
    }
}