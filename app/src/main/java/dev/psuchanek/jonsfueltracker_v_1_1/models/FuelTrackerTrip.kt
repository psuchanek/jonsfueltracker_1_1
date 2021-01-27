package dev.psuchanek.jonsfueltracker_v_1_1.models

import androidx.lifecycle.Transformations.map
import androidx.room.ColumnInfo
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.other.convertTimestampToDateString
import dev.psuchanek.jonsfueltracker_v_1_1.other.convertToGallons
import dev.psuchanek.jonsfueltracker_v_1_1.other.formatDateForUI
import dev.psuchanek.jonsfueltracker_v_1_1.other.round

data class FuelTrackerTrip(
    val id: Int,
    val vehicleId: Int,
    var timestamp: Long? = 0L,
    val fuelVolume: Float,
    val fuelCost: Float,
    val tripMileage: Float,
    val currentMileage: Int,
    val costPerLitre: Float,
    val gasStationName: String
) {

    val vehicleName = when (vehicleId) {
        1 -> R.string.nissan_micra
        2 -> R.string.midget
        3 -> R.string.mercedes_sprinter
        else -> R.string.unknown_car
    }

    val date = timestamp?.formatDateForUI()
    val fillUpCost = "£${this.fuelCost}"
    val lastTripDistance = "${this.tripMileage}"
    val milesPerGallon = (tripMileage / fuelVolume.convertToGallons()).round(2).toString()
}

fun FuelTrackerTrip.asDatabaseModel() = LocalFuelTrackerTrip(
    id = this.id,
    vehicleId = this.vehicleId,
    date = this.timestamp!!.convertTimestampToDateString(),
    fuelVolume = this.fuelVolume,
    fuelCost = this.fuelCost,
    tripMileage = this.tripMileage,
    currentMileage = this.currentMileage,
    costPerLitre = this.costPerLitre,
    gasStationName = this.gasStationName,
    timestamp = this.timestamp
)


fun FuelTrackerTrip.asDomainModel() = NetworkFuelTrackerTrip(
    vehicleID = this.vehicleId,
    date = this.timestamp!!.convertTimestampToDateString(),
    fuelVolume = this.fuelVolume,
    fuelCost = this.fuelCost,
    tripMileage = this.tripMileage,
    currentMileage = this.currentMileage,
    costPerLitre = this.costPerLitre,
    gasStationName = this.gasStationName
)