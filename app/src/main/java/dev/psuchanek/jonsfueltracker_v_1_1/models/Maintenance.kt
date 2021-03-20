package dev.psuchanek.jonsfueltracker_v_1_1.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.psuchanek.jonsfueltracker_v_1_1.utils.formatDateForUI
import dev.psuchanek.jonsfueltracker_v_1_1.utils.round
import java.util.*

@Entity(tableName = "maintenance_table")
data class Maintenance(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "maintenance_id")
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    @ColumnInfo(name = "vehicle_id")
    val vehicleID: Int,
    @ColumnInfo(name = "workshop_name")
    val workshopName: String,
    @ColumnInfo(name = "work_done_description")
    val workDoneDescription: String,
    @ColumnInfo(name = "price")
    val workPrice: Float,
    @ColumnInfo(name = "current_mileage")
    val currentMileage: Int
) {
    var date = timestamp.formatDateForUI()
    var maintenancePrice = "£${workPrice.round(2)}"
    var mileage = currentMileage.toString()
}