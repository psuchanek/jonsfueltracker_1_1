package dev.psuchanek.jonsfueltracker_v_1_1.services.network

import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerModels
import retrofit2.Response
import retrofit2.http.GET


interface FuelTrackerService {
@GET("networkFuelTrackerTrip")
suspend fun getFuelTrackerHistory(): Response<FuelTrackerModels>
}