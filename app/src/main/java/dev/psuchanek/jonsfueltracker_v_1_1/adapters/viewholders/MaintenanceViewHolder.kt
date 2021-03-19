package dev.psuchanek.jonsfueltracker_v_1_1.adapters.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.MaintenanceListItemBinding
import dev.psuchanek.jonsfueltracker_v_1_1.models.Maintenance

class MaintenanceViewHolder(private val binding: MaintenanceListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(maintenance: Maintenance) {
        binding.maintenance = maintenance
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): MaintenanceViewHolder {
            val binding = DataBindingUtil.inflate<MaintenanceListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.maintenance_list_item,
                parent,
                false
            )
            return MaintenanceViewHolder(binding)
        }
    }
}