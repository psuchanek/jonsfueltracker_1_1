package dev.psuchanek.jonsfueltracker_v_1_1.models

import androidx.lifecycle.Transformations.map
import androidx.room.ColumnInfo
import dev.psuchanek.jonsfueltracker_v_1_1.other.convertTimestampToDateString

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
}

fun FuelTrackerTrip.asDatabaseModel()= LocalFuelTrackerTrip(
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