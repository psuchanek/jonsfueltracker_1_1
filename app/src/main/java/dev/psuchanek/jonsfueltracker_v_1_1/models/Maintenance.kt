package dev.psuchanek.jonsfueltracker_v_1_1.models

import androidx.room.Entity
import java.util.*

@Entity(tableName = "maintenance_table")
data class Maintenance(
    val id: String = UUID.randomUUID().toString()
)