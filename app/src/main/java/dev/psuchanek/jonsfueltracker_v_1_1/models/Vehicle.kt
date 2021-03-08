package dev.psuchanek.jonsfueltracker_v_1_1.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.util.*

@Entity(tableName = "vehicle_table")
data class Vehicle(
    @PrimaryKey(autoGenerate = false)
    @Json(name = "vehicle_id")
    val id: String = UUID.randomUUID().toString(),
    @Json(name = "vehicle_name")
    val vehicleName: String,
    @Json(name = "miles_per_litre_avg")
    val avgMPL: Float,
    @Json(name = "miles_per_gallon_avg")
    val avgMPG: Float,
    @Json(name = "pence_per_litre_avg")
    val avgPencePerLitre: Float,
    @Json(name = "pence_per_mile_avg")
    val avgPencePerMile: Float,
    @Json(name = "last_known_mileage")
    val currentMileage: Int
)