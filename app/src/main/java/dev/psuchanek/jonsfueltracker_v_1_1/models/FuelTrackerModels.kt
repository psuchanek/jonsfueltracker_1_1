package dev.psuchanek.jonsfueltracker_v_1_1.models

import com.squareup.moshi.Json
import dev.psuchanek.jonsfueltracker_v_1_1.other.convertDateStringToTimestamp

data class FuelTrackerModels(
    val networkTrips: List<NetworkFuelTrackerTrip>
)

data class NetworkFuelTrackerTrip(
    @Json(name = "record_id")
    val id: Int,

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
    val currentMileage: Float,

    @Json(name = "pence_per_litre")
    val costPerLitre: Float,

    @Json(name = "station")
    val gasStationName: String
)

fun FuelTrackerModels.asDatabaseModel(): List<LocalFuelTrackerTrip> {
    return this.networkTrips.map {
        LocalFuelTrackerTrip(
            id = it.id,
            vehicleId = it.vehicleID,
            date = it.date,
            fuelVolume = it.fuelVolume,
            fuelCost = it.fuelCost,
            tripMileage = it.tripMileage,
            costPerLitre = it.costPerLitre,
            gasStationName = it.gasStationName,
            timestamp = it.date.convertDateStringToTimestamp()
        )
    }
}