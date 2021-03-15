package dev.psuchanek.jonsfueltracker_v_1_1.repositories

import androidx.lifecycle.LiveData
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.Vehicle

interface Repository {

    suspend fun insertTrip(trip: FuelTrackerTrip)
    suspend fun getLastKnownMileage(vehicleId: Int): Long?
    val observeAllVehicles: LiveData<List<Vehicle>>

}