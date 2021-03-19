package dev.psuchanek.jonsfueltracker_v_1_1.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.Maintenance
import dev.psuchanek.jonsfueltracker_v_1_1.models.Vehicle
import dev.psuchanek.jonsfueltracker_v_1_1.utils.ViewHolderType

class GenericListAdapter<T : Any>(private val viewHolderType: ViewHolderType) :
    ListAdapter<T, RecyclerView.ViewHolder>(GenericItemCallback<T>()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Vehicle -> {
                (holder as VehicleViewHolder).bind(item as Vehicle)
            }
            is FuelTrackerTrip -> {
                (holder as FuelTrackerAdapter.ViewHolder).bind(item as FuelTrackerTrip)
            }
            is Maintenance -> {
                (holder as MaintenanceViewHolder).bind(item as Maintenance)
            }
        }

    }
}