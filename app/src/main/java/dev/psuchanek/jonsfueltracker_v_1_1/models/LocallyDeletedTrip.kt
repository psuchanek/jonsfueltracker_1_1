package dev.psuchanek.jonsfueltracker_v_1_1.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locally_deleted_trips_ids")
data class LocallyDeletedTrip(
    @PrimaryKey
    val deleteTripID: String
)