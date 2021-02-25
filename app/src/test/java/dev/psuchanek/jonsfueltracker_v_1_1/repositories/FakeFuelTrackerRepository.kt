package dev.psuchanek.jonsfueltracker_v_1_1.repositories

import androidx.lifecycle.MutableLiveData
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip

class FakeFuelTrackerRepository: Repository{


    private val fuelTrackerTrips = mutableListOf<FuelTrackerTrip>()

    private val observableFuelTrackerTrips =
        MutableLiveData<List<FuelTrackerTrip>>(fuelTrackerTrips)



    private fun refreshLiveData() {
        observableFuelTrackerTrips.postValue(fuelTrackerTrips)
    }

    override suspend fun insertTrip(trip: FuelTrackerTrip) {

    }



//    override suspend fun getTripsSortedById(): List<LocalFuelTrackerTrip> {
//        return listOf()
//    }
//
//    override suspend fun getMostRecentTripRecord(): LocalFuelTrackerTrip {
//        return LocalFuelTrackerTrip(
//            id = fuelTrackerTrips[fuelTrackerTrips.lastIndex].id,
//            vehicleId = fuelTrackerTrips[fuelTrackerTrips.lastIndex].vehicleId,
//            date = fuelTrackerTrips[fuelTrackerTrips.lastIndex].date!!,
//            fuelVolume = fuelTrackerTrips[fuelTrackerTrips.lastIndex].fuelVolume,
//            fuelCost = fuelTrackerTrips[fuelTrackerTrips.lastIndex].fuelCost,
//            tripMileage = fuelTrackerTrips[fuelTrackerTrips.lastIndex].tripMileage,
//            currentMileage = fuelTrackerTrips[fuelTrackerTrips.lastIndex].currentMileage,
//            costPerLitre = fuelTrackerTrips[fuelTrackerTrips.lastIndex].costPerLitre,
//            gasStationName = fuelTrackerTrips[fuelTrackerTrips.lastIndex].gasStationName,
//            timestamp = fuelTrackerTrips[fuelTrackerTrips.lastIndex].timestamp
//        )
//    }
//
//    override suspend fun fetchData(): Status {
//        return Status.SUCCESS
//    }
//
//    override suspend fun insertFuelTrackerTrip(trip: FuelTrackerTrip): Status {
//        return Status.SUCCESS
//    }
//
//    override suspend fun updateService(): Status {
//        return Status.SUCCESS
//    }
//
//    override suspend fun updateFuelTrackerTrip(trip: FuelTrackerTrip) {
//    }
//
//    override suspend fun getLastKnownMileage(vehicleId: Int): Long {
//        return 0L
//    }
//
//    override suspend fun getAllTripsSortedByTimestampRange(
//        start: Long,
//        end: Long
//    ): List<LocalFuelTrackerTrip> {
//        return listOf()
//    }
}