package dev.psuchanek.jonsfueltracker_v_1_1.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import dev.psuchanek.jonsfueltracker_v_1_1.utils.round
import java.util.*

@Entity(tableName = "vehicle_table")
data class Vehicle(
    @PrimaryKey(autoGenerate = false)
    @Json(name = "vehicle_id")
    val id: String = UUID.randomUUID().toString(),
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
    val currentMileage: Int = 0
) {
    var vehicleMileage = "$currentMileage"
    var avgMPLString = "${avgMPL.round(2)}"
    var avgMPGString = "${avgMPG.round(2)}"
    var avgPPMString = "${avgPencePerMile.round(2)}"
    var avgPPLString = "${avgPencePerLitre.round(2)}"
}