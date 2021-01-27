package dev.psuchanek.jonsfueltracker_v_1_1.repositories

import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.LocalFuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.other.Status

interface Repository {
    val getTripsSortedById: List<LocalFuelTrackerTrip>
    val getMostRecentTripRecord: LocalFuelTrackerTrip

    suspend fun fetchData(): Status

    suspend fun insertFuelTrackerTrip(trip: FuelTrackerTrip): Status

    suspend fun updateService(): Status

    suspend fun updateFuelTrackerTrip(trip: FuelTrackerTrip)
    fun getAllTripsSortedByTimestampRange(start: Long, end: Long): List<LocalFuelTrackerTrip>
}