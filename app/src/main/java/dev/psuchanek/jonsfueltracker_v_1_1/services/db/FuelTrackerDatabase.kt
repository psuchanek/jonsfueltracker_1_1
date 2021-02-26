package dev.psuchanek.jonsfueltracker_v_1_1.services.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.psuchanek.jonsfueltracker_v_1_1.models.LocalFuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.LocallyDeletedTrip

@Database(entities = [LocalFuelTrackerTrip::class, LocallyDeletedTrip::class] , version = 1, exportSchema = false)
abstract class FuelTrackerDatabase: RoomDatabase() {
    abstract fun fuelTrackerDao(): FuelTrackerDao
}