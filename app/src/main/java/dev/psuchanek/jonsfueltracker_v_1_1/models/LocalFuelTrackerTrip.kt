package dev.psuchanek.jonsfueltracker_v_1_1.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fuel_tracker_history_table")
data class LocalFuelTrackerTrip(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "vehicle_id")
    val vehicleId: Int,

    @ColumnInfo(name = "date")
    var date: String,

    @ColumnInfo(name = "fuel_volume")
    val fuelVolume: Float,

    @ColumnInfo(name = "fuel_cost")
    val fuelCost: Float,

    @ColumnInfo(name = "trip_mileage")
    val tripMileage: Float,

    @ColumnInfo(name = "current_mileage")
    val currentMileage: Int,

    @ColumnInfo(name = "cost_per_litre")
    val costPerLitre: Float,

    @ColumnInfo(name = "gas_station_name")
    val gasStationName: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long? = 0L
) {

}

fun List<LocalFuelTrackerTrip>.asFuelTrackerTripModel(): List<FuelTrackerTrip> {
    return this.map {
        FuelTrackerTrip(
            id = it.id,
            vehicleId = it.vehicleId,
            timestamp = it.timestamp,
            fuelVolume = it.fuelVolume,
            fuelCost = it.fuelCost,
            tripMileage = it.tripMileage,
            currentMileage = it.currentMileage,
            costPerLitre = it.costPerLitre,
            gasStationName = it.gasStationName
        )
    }
}