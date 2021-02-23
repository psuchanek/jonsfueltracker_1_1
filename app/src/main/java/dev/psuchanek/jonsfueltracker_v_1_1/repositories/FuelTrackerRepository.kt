package dev.psuchanek.jonsfueltracker_v_1_1.repositories

import android.app.Application
import dev.psuchanek.jonsfueltracker_v_1_1.models.*
import dev.psuchanek.jonsfueltracker_v_1_1.services.db.FuelTrackerDao
import dev.psuchanek.jonsfueltracker_v_1_1.services.network.FuelTrackerService
import dev.psuchanek.jonsfueltracker_v_1_1.utils.checkNetworkConnection
import dev.psuchanek.jonsfueltracker_v_1_1.utils.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FuelTrackerRepository @Inject constructor(
    private val fuelTrackerDao: FuelTrackerDao,
    private val apiService: FuelTrackerService,
    private val context: Application
) : Repository {

    private var currentFuelTrackerResponse: Response<NetworkDataResponse>? = null

    fun getAllNotes() = networkBoundResource(
        query = {
            fuelTrackerDao.getAllByTimestamp()
        },
        fetch = {
            syncTrips()
            currentFuelTrackerResponse
        },
        saveFetchResult = { response ->
            response?.body()?.let { listOfTrips ->
                val localModelList = listOfTrips.asDatabaseModel()
                insertTrips(localModelList.onEach { trip -> trip.isSynced = true })
            }

        },
        shouldFetch = {
            checkNetworkConnection(context)
        }
    )

    private suspend fun syncTrips() {

        val unsyncedTrips = fuelTrackerDao.getAllUnsyncedTrips()
        unsyncedTrips.forEach { trip -> insertTrip(trip.asFuelTrackerTrip()) }

        currentFuelTrackerResponse = apiService.getFuelTrackerHistory()
        currentFuelTrackerResponse?.body()?.let { response ->
            fuelTrackerDao.deleteAllTrips()
            insertTrips(response.asDatabaseModel().onEach { trip -> trip.isSynced = true })
        }
    }


    private suspend fun insertTrips(trips: List<LocalFuelTrackerTrip>) {
        trips.forEach { trip ->
            insertTrip(trip.asFuelTrackerTrip())
        }
    }

    override suspend fun insertTrip(trip: FuelTrackerTrip) {
        val response = try {
            apiService.addFuelTrackerTrip(trip.asDomainModel())
        } catch (e: Exception) {
            null
        }
        if (response != null && response.isSuccessful) {
            fuelTrackerDao.insertFuelTrackerTrip(trip.apply { isSynced = true }.asDatabaseModel())
        } else {
            fuelTrackerDao.insertFuelTrackerTrip(trip.asDatabaseModel())
        }

    }

    fun observeTripById(tripId: Int) = fuelTrackerDao.observerTripById(tripId)

    override suspend fun getLastKnownMileage(vehicleId: Int) = withContext(Dispatchers.IO) {
        fuelTrackerDao.getLastKnownMileage(vehicleId)
    }

    suspend fun getAllTripsSortedByTimeStamp() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllByTimestamp() }

    suspend fun getAllTripsSortedByFuelVolume() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllByFuelVolume() }

    suspend fun getAllTripsSortedByFuelCost() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllByFuelCost() }

    suspend fun getAllTripsSortedByTripMileage() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllByTripMileage() }
}


