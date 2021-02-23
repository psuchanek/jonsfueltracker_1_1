package dev.psuchanek.jonsfueltracker_v_1_1.repositories

import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.asDatabaseModel
import dev.psuchanek.jonsfueltracker_v_1_1.models.asDomainModel
import dev.psuchanek.jonsfueltracker_v_1_1.utils.Status
import dev.psuchanek.jonsfueltracker_v_1_1.services.db.FuelTrackerDao
import dev.psuchanek.jonsfueltracker_v_1_1.services.network.FuelTrackerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FuelTrackerRepository @Inject constructor(
    private val fuelTrackerDao: FuelTrackerDao,
    private val apiService: FuelTrackerService
) : Repository {

    override suspend fun getTripsSortedById() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllTripsSortedById() }

    suspend fun getAllTripsSortedByTimeStamp() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllTripsSortedByTimeStamp() }

    suspend fun getAllTripsSortedByFuelVolume() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllTripsSortedByFuelVolume() }

    suspend fun getAllTripsSortedByFuelCost() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllTripsSortedByFuelCost() }

    suspend fun getAllTripsSortedByTripMileage() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getAllTripsSortedByTripMileage() }

    override suspend fun getMostRecentTripRecord() =
        withContext(Dispatchers.IO) { fuelTrackerDao.getMostRecentTripRecord() }

    override suspend fun fetchData() = insertListFromNetworkCall()


    private suspend fun insertListFromNetworkCall(): Status {
        var responseStatus = Status.LOADING
        withContext(Dispatchers.IO) {
            try {
                val apiResponse = apiService.getFuelTrackerHistory()
                if (apiResponse.isSuccessful) {
                    if (apiResponse.body() == null || apiResponse.code() == 204) {
                        return@withContext Status.NONE

                    }
                    fuelTrackerDao.insertFuelTrackerHistory(
                        apiResponse.body()!!.asDatabaseModel()
                    )
                    responseStatus = Status.SUCCESS

                } else {

                }

            } catch (e: Exception) {
                Timber.d("DEBUG: fuelTrackerRepository: insertListFromNetworkCall: ${e.message}")
                Timber.d("DEBUG: fuelTrackerRepository: insertListFromNetworkCall: ${e.cause}")
                responseStatus = Status.ERROR
            }
        }

        return responseStatus
    }

    override suspend fun insertFuelTrackerTrip(trip: FuelTrackerTrip): Status {
        withContext(Dispatchers.IO) {
            apiService.postFuelTrackerTrip(trip.asDomainModel())
        }
        return postToService(trip)
    }

    override suspend fun updateService(): Status {
        return Status.SUCCESS
    }

    override suspend fun updateFuelTrackerTrip(trip: FuelTrackerTrip) {
        withContext(Dispatchers.IO) {
            fuelTrackerDao.updateTrip(trip.asDatabaseModel())
        }
    }

    override suspend fun getLastKnownMileage(vehicleId: Int) =
        withContext(Dispatchers.IO) {
            fuelTrackerDao.getLastKnownMileage(vehicleId)
        }


    override suspend fun getAllTripsSortedByTimestampRange(start: Long, end: Long) =
        fuelTrackerDao.getAllTripsByTimestampRange(start, end)

    private suspend fun postToService(trip: FuelTrackerTrip, shouldPost: Boolean = false): Status {
        var status: Status
        if (shouldPost) {
            withContext(Dispatchers.IO) {
                try {
                    val apiResponse = apiService.postFuelTrackerTrip(trip.asDomainModel())
                    if (apiResponse.isSuccessful || apiResponse.code() == 204) {
                        if (apiResponse.body() != null) {
                            status = Status.SUCCESS
                        } else {
                            throw HttpException(Response.success(apiResponse.body()))
                        }
                    } else {
                        throw IOException()
                    }

                } catch (e: IOException) {
                    Timber.d("DEBUG: fuelTrackerRepository: insertFuelTrackerTrip: ${e.message}")
                    status = Status.ERROR
                } catch (e: HttpException) {
                    Timber.d("DEBUG: fuelTrackerRepository: insertFuelTrackerTrip: ${e.code()}")
                    Timber.d(
                        "DEBUG: fuelTrackerRepository: insertFuelTrackerTrip: ${e.response()
                            ?.body()}"
                    )
                    status = Status.NETWORK_ERROR

                }
            }
        } else {
            status = Status.NONE
        }

        return status
    }
}