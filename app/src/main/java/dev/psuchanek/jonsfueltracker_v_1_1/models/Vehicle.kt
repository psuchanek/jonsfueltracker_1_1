package dev.psuchanek.jonsfueltracker_v_1_1.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import dev.psuchanek.jonsfueltracker_v_1_1.models.responses.NetworkVehicle
import dev.psuchanek.jonsfueltracker_v_1_1.utils.round

@Entity(tableName = "vehicle_table")
data class Vehicle(
    @PrimaryKey(autoGenerate = true)
    @Json(name = "vehicle_id")
    val id: Int = 0,
    @Json(name = "vehicle_name")
    val vehicleName: String = "No name",
    @Json(name = "miles_per_litre_avg")
    val avgMPL: Float = 0.0f,
    @Json(name = "miles_per_gallon_avg")
    val avgMPG: Float = 0.0f,
    @Json(name = "pence_per_litre_avg")
    val avgPencePerLitre: Float = 0.0f,
    @Json(name = "pence_per_mile_avg")
    val avgPencePerMile: Float = 0.0f,
    @Json(name = "last_known_mileage")
    val currentMileage: Int = 0,
    var isSynced: Boolean = false
) {
    var vehicleMileage = "$currentMileage"
    var avgMPLString = "${avgMPL.round(2)}"
    var avgMPGString = "${avgMPG.round(2)}"
    var avgPPMString = "${avgPencePerMile.round(2)}"
    var avgPPLString = "${avgPencePerLitre.round(2)}"
}

fun Vehicle.asNetworkModel(): NetworkVehicle {
    return NetworkVehicle(vehicleName = this.vehicleName)
}