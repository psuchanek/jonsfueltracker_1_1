package dev.psuchanek.jonsfueltracker_v_1_1.services.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.psuchanek.jonsfueltracker_v_1_1.models.Trip

@Database(entities = [Trip::class] , version = 2, exportSchema = false)
abstract class FuelTrackerDatabase: RoomDatabase() {
    abstract fun fuelTrackerDao(): FuelTrackerDao
}