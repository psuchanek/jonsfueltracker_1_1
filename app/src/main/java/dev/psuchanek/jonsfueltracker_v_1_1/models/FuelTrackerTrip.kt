package dev.psuchanek.jonsfueltracker_v_1_1.models

import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.models.responses.NetworkFuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.utils.convertTimestampToDateString
import dev.psuchanek.jonsfueltracker_v_1_1.utils.convertToGallons
import dev.psuchanek.jonsfueltracker_v_1_1.utils.formatDateForUI
import dev.psuchanek.jonsfueltracker_v_1_1.utils.round
import java.util.*

data class FuelTrackerTrip(
    val id: String = UUID.randomUUID().toString(),
    val vehicleId: Int,
    var timestamp: Long,
    val fuelVolume: Float,
    val fuelCost: Float,
    val tripMileage: Float,
    val currentMileage: Int,
    val costPerLitre: Float,
    val gasStationName: String,
    var isSynced: Boolean = false,
    var isExpanded: Boolean = false
) {

    val vehicleName = when (vehicleId) {
        1 -> R.string.nissan_micra
        2 -> R.string.midget
        3 -> R.string.mercedes_sprinter
        else -> R.string.unknown_car
    }

    val litres = "Fuel volume: $fuelVolume litres"
    val date = timestamp.formatDateForUI()
    val fillUpCost = "£${this.fuelCost}"
    val lastTripDistance = "Trip distance: ${this.tripMileage} miles"
    val ppl = "PPL: $costPerLitre"
    val milesPerGallon = "MPG: ${(tripMileage / fuelVolume.convertToGallons()).round(2)}"
    val sync = when (isSynced) {
        true -> "Synced"
        false -> "Not Synced"
    }

    val dashMiles = "${this.tripMileage} Miles"
    val dashCost = "£${this.fuelCost}"
    val dashMPG = "${(this.tripMileage / this.fuelVolume.convertToGallons()).round(2)}"
}

fun FuelTrackerTrip.asDatabaseModel() = LocalFuelTrackerTrip(
    id = this.id,
    vehicleId = this.vehicleId,
    fuelVolume = this.fuelVolume,
    fuelCost = this.fuelCost,
    tripMileage = this.tripMileage,
    currentMileage = this.currentMileage,
    costPerLitre = this.costPerLitre,
    gasStationName = this.gasStationName,
    timestamp = this.timestamp,
    isSynced = this.isSynced
)


fun FuelTrackerTrip.asDomainModel() =
    NetworkFuelTrackerTrip(
        id = this.id,
        vehicleID = this.vehicleId,
        date = this.timestamp.convertTimestampToDateString(),
        fuelVolume = this.fuelVolume,
        fuelCost = this.fuelCost,
        tripMileage = this.tripMileage,
        currentMileage = this.currentMileage,
        costPerLitre = this.costPerLitre,
        gasStationName = this.gasStationName
    )