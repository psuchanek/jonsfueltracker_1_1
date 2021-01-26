package dev.psuchanek.jonsfueltracker_v_1_1.services.db

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.psuchanek.jonsfueltracker_v_1_1.models.Trip

@Dao
interface FuelTrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelTrackerHistory(fuelTrackerHistory: List<Trip>)

    @Update
    suspend fun updateTrip(trip: Trip)

    @Query("SELECT * FROM fuel_tracker_history_table ORDER BY id DESC")
    fun getAllTripsSortedById(): List<Trip>
}