package dev.psuchanek.jonsfueltracker_v_1_1.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.psuchanek.jonsfueltracker_v_1_1.adapters.viewholders.MaintenanceViewHolder
import dev.psuchanek.jonsfueltracker_v_1_1.adapters.viewholders.TripHistoryViewHolder
import dev.psuchanek.jonsfueltracker_v_1_1.adapters.viewholders.VehicleViewHolder
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.Maintenance
import dev.psuchanek.jonsfueltracker_v_1_1.models.Vehicle
import dev.psuchanek.jonsfueltracker_v_1_1.utils.MAINTENANCE
import dev.psuchanek.jonsfueltracker_v_1_1.utils.TRIP_HISTORY
import dev.psuchanek.jonsfueltracker_v_1_1.utils.VEHICLE

class FuelTrackerListAdapter<T : Any>(private val viewHolderType: Int) :
    ListAdapter<T, RecyclerView.ViewHolder>(GenericItemCallback<T>()) {

    override fun getItemViewType(position: Int) = viewHolderType
    override fun getItemCount() = currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VEHICLE -> {
                VehicleViewHolder.from(parent)
            }
            MAINTENANCE -> {
                MaintenanceViewHolder.from(parent)
            }
            TRIP_HISTORY -> {
                TripHistoryViewHolder.from(parent)
            }
            else -> throw Exception("No ViewHolder found")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Vehicle -> {
                (holder as VehicleViewHolder).bind(item as Vehicle)
            }
            is FuelTrackerTrip -> {
                (holder as TripHistoryViewHolder).bind(item as FuelTrackerTrip)
            }
            is Maintenance -> {
                (holder as MaintenanceViewHolder).bind(item as Maintenance)
            }
        }

    }
}