package dev.psuchanek.jonsfueltracker_v_1_1.services.network

import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.NetworkDataResponse
import dev.psuchanek.jonsfueltracker_v_1_1.models.NetworkFuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.other.TimePeriod
import dev.psuchanek.jonsfueltracker_v_1_1.other.convertTimestampToDateString
import dev.psuchanek.jonsfueltracker_v_1_1.other.getTimePeriodTimestamp
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import retrofit2.Response

class FakeFuelTrackerApi  {

//    private val fuelTrackerModel = mutableMapOf<String, FakeNetworkDataResponse>()
//    var errorMsg: String? = null
//
//    suspend fun postTrip(trip: NetworkFuelTrackerTrip) = runBlocking {
//        val networkFuelTrackerTrip = fuelTrackerModel.getOrPut() {
//            NetworkDataResponse(networkTrips = emptyList())
//        }
//
//        networkFuelTrackerTrip.networkTrips.add(trip)
//    }
//
//    override suspend fun getFuelTrackerHistory(): Response<NetworkDataResponse> {
//
//    }
//
//    override suspend fun postFuelTrackerTrip(trip: NetworkFuelTrackerTrip): Response<NetworkDataResponse> {
//
//    }
//
//    private class FakeNetworkDataResponse(val networkTrips: MutableList<NetworkFuelTrackerTrip> = arrayListOf()) {
//
//    }
}