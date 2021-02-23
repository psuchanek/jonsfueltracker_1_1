package dev.psuchanek.jonsfueltracker_v_1_1.repositories

import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip

interface Repository {

    suspend fun insertTrip(trip: FuelTrackerTrip)
    suspend fun getLastKnownMileage(vehicleId: Int): Long?

}