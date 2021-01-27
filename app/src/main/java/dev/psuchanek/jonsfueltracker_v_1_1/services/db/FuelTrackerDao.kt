package dev.psuchanek.jonsfueltracker_v_1_1.services.db

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.psuchanek.jonsfueltracker_v_1_1.models.LocalFuelTrackerTrip

@Dao
interface FuelTrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelTrackerHistory(fuelTrackerHistory: List<LocalFuelTrackerTrip>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelTrackerTrip(trip: LocalFuelTrackerTrip)

    @Update
    suspend fun updateTrip(localFuelTrackerTrip: LocalFuelTrackerTrip)

    @Query("SELECT * FROM fuel_tracker_history_table ORDER BY id DESC")
    fun getAllTripsSortedById(): List<LocalFuelTrackerTrip>

    @Query("SELECT * FROM fuel_tracker_history_table ORDER BY timestamp DESC")
    fun getAllTripsSortedByTimeStamp(): List<LocalFuelTrackerTrip>

    @Query("SELECT * FROM fuel_tracker_history_table ORDER BY fuel_volume DESC")
    fun getAllTripsSortedByFuelVolume(): List<LocalFuelTrackerTrip>

    @Query("SELECT * FROM fuel_tracker_history_table ORDER BY fuel_cost DESC")
    fun getAllTripsSortedByFuelCost(): List<LocalFuelTrackerTrip>

    @Query("SELECT * FROM fuel_tracker_history_table ORDER BY trip_mileage DESC")
    fun getAllTripsSortedByTripMileage(): List<LocalFuelTrackerTrip>

    @Query("SELECT * FROM fuel_tracker_history_table WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    fun getAllTripsByTimestampRange(start: Long, end: Long): List<LocalFuelTrackerTrip>

    @Query("SELECT * FROM fuel_tracker_history_table ORDER by timestamp DESC LIMIT 1")
    fun getMostRecentTripRecord(): LocalFuelTrackerTrip
}