package dev.psuchanek.jonsfueltracker_v_1_1.repositories

import dev.psuchanek.jonsfueltracker_v_1_1.models.asDatabaseModel
import dev.psuchanek.jonsfueltracker_v_1_1.other.OnSuccessCache
import dev.psuchanek.jonsfueltracker_v_1_1.other.Status
import dev.psuchanek.jonsfueltracker_v_1_1.services.db.FuelTrackerDao
import dev.psuchanek.jonsfueltracker_v_1_1.services.network.FuelTrackerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FuelTrackerRepository @Inject constructor(
    private val fuelTrackerDao: FuelTrackerDao,
    private val apiService: FuelTrackerService
) {

    val getTripsSortedById = fuelTrackerDao.getAllTripsSortedById()

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

    private var fuelTrackerHistoryCache = OnSuccessCache(
        onErrorFallback = { listOf<String>() }
    ) {
        apiService.getFuelTrackerHistory()
    }


}