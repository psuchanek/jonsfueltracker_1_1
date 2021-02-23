package dev.psuchanek.jonsfueltracker_v_1_1.repositories

import android.app.Application
import dev.psuchanek.jonsfueltracker_v_1_1.services.db.FuelTrackerDao
import dev.psuchanek.jonsfueltracker_v_1_1.services.network.FuelTrackerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FuelTrackerRepository @Inject constructor(
    private val fuelTrackerDao: FuelTrackerDao,
    private val apiService: FuelTrackerService,
    private val context: Application
) {

    suspend fun getTripsSortedById() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllTripsSortedById() }

    suspend fun getAllTripsSortedByTimeStamp() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllTripsSortedByTimeStamp() }

    suspend fun getAllTripsSortedByFuelVolume() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllTripsSortedByFuelVolume() }

    suspend fun getAllTripsSortedByFuelCost() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllTripsSortedByFuelCost() }

    suspend fun getAllTripsSortedByTripMileage() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllTripsSortedByTripMileage() }
}


