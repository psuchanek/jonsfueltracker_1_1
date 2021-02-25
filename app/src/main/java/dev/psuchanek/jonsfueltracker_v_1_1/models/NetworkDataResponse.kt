package dev.psuchanek.jonsfueltracker_v_1_1.models

import com.squareup.moshi.Json
import dev.psuchanek.jonsfueltracker_v_1_1.utils.convertDateStringToTimestamp

data class NetworkDataResponse(
    @Json(name = "history")
    val networkTrips: List<NetworkFuelTrackerTrip>
)

data class NetworkFuelTrackerTrip(
    @Json(name = "vehicle_id")
    val vehicleID: Int,

    @Json(name = "record_date")
    val date: String,

    @Json(name = "litres")
    val fuelVolume: Float,

    @Json(name = "cost")
    val fuelCost: Float,

    @Json(name = "trip")
    val tripMileage: Float,

    @Json(name = "mileage")
    val currentMileage: Int,

    @Json(name = "pence_per_litre")
    val costPerLitre: Float,

    @Json(name = "station")
    val gasStationName: String
)

fun NetworkDataResponse.asDatabaseModel(): List<LocalFuelTrackerTrip> {
    return this.networkTrips.map {
        LocalFuelTrackerTrip(
            vehicleId = it.vehicleID,
            fuelVolume = it.fuelVolume,
            fuelCost = it.fuelCost,
            tripMileage = it.tripMileage,
            currentMileage = it.currentMileage,
            costPerLitre = it.costPerLitre,
            gasStationName = it.gasStationName,
            timestamp = it.date.convertDateStringToTimestamp()
        )
    }
}