package dev.psuchanek.jonsfueltracker_v_1_1.models

import androidx.room.ColumnInfo

data class FuelTrackerTrip(
    val id: Int,
    val vehicleId: Int,
    var timestamp: Long? = 0L,
    val fuelVolume: Float,
    val fuelCost: Float,
    val tripMileage: Float,
    val costPerLitre: Float,
    val gasStationName: String
) {
}