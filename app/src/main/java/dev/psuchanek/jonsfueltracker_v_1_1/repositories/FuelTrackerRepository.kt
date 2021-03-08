package dev.psuchanek.jonsfueltracker_v_1_1.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import dev.psuchanek.jonsfueltracker_v_1_1.models.*
import dev.psuchanek.jonsfueltracker_v_1_1.models.requests.TripDeleteRequest
import dev.psuchanek.jonsfueltracker_v_1_1.models.responses.NetworkDataResponse
import dev.psuchanek.jonsfueltracker_v_1_1.models.responses.asDatabaseModel
import dev.psuchanek.jonsfueltracker_v_1_1.services.db.FuelTrackerDao
import dev.psuchanek.jonsfueltracker_v_1_1.services.network.FuelTrackerService
import dev.psuchanek.jonsfueltracker_v_1_1.utils.checkNetworkConnection
import dev.psuchanek.jonsfueltracker_v_1_1.utils.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FuelTrackerRepository @Inject constructor(
    private val fuelTrackerDao: FuelTrackerDao,
    private val apiService: FuelTrackerService,
    private val context: Application
) : Repository {

    private var currentFuelTrackerResponse: Response<NetworkDataResponse>? = null

    fun getAllTrips() = networkBoundResource(
        query = {
            fuelTrackerDao.getAllByTimestamp()
        },
        fetch = {
            syncTrips()
            currentFuelTrackerResponse
        },
        onFetchFailed = {
            Timber.d("DEBUG: reason for server not reached: ${it.message}")
        },
        saveFetchResult = { response ->
            response?.let { listOfTrips ->
                currentFuelTrackerResponse = listOfTrips
                currentFuelTrackerResponse?.body()?.let { response ->
                    val currentDatabaseList =
                        withContext(Dispatchers.IO) { fuelTrackerDao.getAllById() }
                    val currentDatabaseListIDs = currentDatabaseList.map { it.id }
                    val responseIDs = response.asDatabaseModel().map { it.id }
                    val differenceFromNetwork =
                        response.asDatabaseModel().filter { it.id !in currentDatabaseListIDs }
                    val differenceFromDatabase =
                        currentDatabaseList.filter { it.id !in responseIDs }
                    if (differenceFromNetwork.isNotEmpty()) {
                        insertTrips(differenceFromNetwork.onEach { trip -> trip.isSynced = true })
                    }
                    if (differenceFromDatabase.isNotEmpty()) {
                        insertTrips(differenceFromDatabase.onEach { trip -> trip.isSynced = true })
                    }
                }
            }

        },
        shouldFetch = {
            checkNetworkConnection(context)
        }
    )

    private suspend fun syncTrips() {
        val locallyDeletedTripIDs = fuelTrackerDao.getAllLocallyDeletedTripIDs()
        locallyDeletedTripIDs.forEach { deletedTrip ->
            deleteTripByID(deletedTrip.deleteTripID)
        }


        val unsyncedTrips = fuelTrackerDao.getAllUnsyncedTrips()
        unsyncedTrips.forEach { trip -> insertTrip(trip.asFuelTrackerTrip()) }
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

    suspend fun deleteTripByID(tripID: String) = withContext(Dispatchers.IO) {
        val response = try {
            apiService.deleteTrip(
                TripDeleteRequest(
                    tripID
                )
            )
        } catch (e: Exception) {
            null
        }
        Timber.d("DEBUG: deleteResponse: ${response?.message()}, ${response?.body()?.info}")
        fuelTrackerDao.deleteTripByID(tripID)
        if (response == null || !response.isSuccessful) {
            fuelTrackerDao.insertLocallyDeletedTripId(LocallyDeletedTrip(tripID))
        } else {
            fuelTrackerDao.deleteLocallyDeletedTripId(tripID)
        }
    }

    suspend fun deleteLocallyDeletedTripID(tripID: String) {
        withContext(Dispatchers.IO) {
            fuelTrackerDao.deleteLocallyDeletedTripId(tripID)
        }
    }

    fun getMostRecentTripRecord(): LiveData<LocalFuelTrackerTrip>? = fuelTrackerDao.getMostRecentTripRecord()

    suspend fun getAllByTimestampRange(start: Long, end: Long): List<LocalFuelTrackerTrip>? =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllByTimestampRange(start, end) }

    fun observeTripById(tripId: Int) = fuelTrackerDao.observerTripById(tripId)

    override suspend fun getLastKnownMileage(vehicleId: Int) =
        withContext(Dispatchers.IO) { fuelTrackerDao.getLastKnownMileage(vehicleId) }

    fun getAllTripsSortedByTimeStamp() = fuelTrackerDao.getAllByTimestamp()

    suspend fun getAllTripsSortedByFuelVolume() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllByFuelVolume() }

    suspend fun getAllTripsSortedByFuelCost() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllByFuelCost() }

    suspend fun getAllTripsSortedByTripMileage() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllByTripMileage() }


}


