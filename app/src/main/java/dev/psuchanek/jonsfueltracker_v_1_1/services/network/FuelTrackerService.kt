package dev.psuchanek.jonsfueltracker_v_1_1.services.network

import dev.psuchanek.jonsfueltracker_v_1_1.models.NetworkDataResponse
import dev.psuchanek.jonsfueltracker_v_1_1.models.NetworkFuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.utils.DELETE_TRIP_FROM_SERVICE
import dev.psuchanek.jonsfueltracker_v_1_1.utils.GET_FROM_SERVICE
import dev.psuchanek.jonsfueltracker_v_1_1.utils.POST_TRIP_TO_SERVICE
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface FuelTrackerService {

    @GET(GET_FROM_SERVICE)
    suspend fun getFuelTrackerHistory(): Response<NetworkDataResponse>

    @POST(POST_TRIP_TO_SERVICE)
    suspend fun addFuelTrackerTrip(
        @Body trip: NetworkFuelTrackerTrip
    ): Response<ResponseBody>

    @POST(DELETE_TRIP_FROM_SERVICE)
    suspend fun deleteTrip(
        @Body record_id: String
    ): Response<ResponseBody>
}