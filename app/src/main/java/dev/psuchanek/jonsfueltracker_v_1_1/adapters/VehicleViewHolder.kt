package dev.psuchanek.jonsfueltracker_v_1_1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.VehicleItemLayoutBinding
import dev.psuchanek.jonsfueltracker_v_1_1.models.Vehicle

class VehicleViewHolder(val binding: VehicleItemLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(vehicle: Vehicle) {
        binding.vehicle = vehicle
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): VehicleViewHolder {
            val binding = DataBindingUtil.inflate<VehicleItemLayoutBinding>(
                LayoutInflater.from(parent.context),
                R.layout.vehicle_item_layout,
                parent,
                false
            )
            return VehicleViewHolder(
                binding
            )
        }
    }
}