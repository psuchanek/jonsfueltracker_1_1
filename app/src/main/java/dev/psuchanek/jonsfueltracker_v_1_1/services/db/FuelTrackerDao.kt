package dev.psuchanek.jonsfueltracker_v_1_1.services.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.psuchanek.jonsfueltracker_v_1_1.models.LocalFuelTrackerTrip
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelTrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelTrackerHistory(fuelTrackerHistory: List<LocalFuelTrackerTrip>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelTrackerTrip(trip: LocalFuelTrackerTrip)

    @Query("SELECT * FROM fuel_tracker_history_table ORDER BY id DESC")
    fun getAllById(): List<LocalFuelTrackerTrip>

    @Query("SELECT * FROM fuel_tracker_history_table ORDER BY timestamp DESC")
    fun getAllByTimestamp(): Flow<List<LocalFuelTrackerTrip>>


    @Query("SELECT * FROM fuel_tracker_history_table WHERE is_synced = 0")
    fun getAllUnsyncedTrips(): List<LocalFuelTrackerTrip>

    @Query("DELETE FROM fuel_tracker_history_table")
    fun deleteAllTrips()

    @Query("SELECT * FROM fuel_tracker_history_table WHERE id = :tripId")
    fun observerTripById(tripId: Int): LiveData<LocalFuelTrackerTrip>?

    //TODO: Focus on the rest here after refactoring

    @Query("SELECT * FROM fuel_tracker_history_table ORDER BY fuel_volume DESC")
    fun getAllByFuelVolume(): List<LocalFuelTrackerTrip>

    @Query("SELECT * FROM fuel_tracker_history_table ORDER BY fuel_cost DESC")
    fun getAllByFuelCost(): List<LocalFuelTrackerTrip>

    @Query("SELECT * FROM fuel_tracker_history_table ORDER BY trip_mileage DESC")
    fun getAllByTripMileage(): List<LocalFuelTrackerTrip>

    @Query("SELECT * FROM fuel_tracker_history_table WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    fun getAllByTimestampRange(start: Long, end: Long): List<LocalFuelTrackerTrip>

    @Query("SELECT * FROM fuel_tracker_history_table ORDER by timestamp DESC LIMIT 1")
    fun getMostRecentTripRecord(): LocalFuelTrackerTrip

    @Query("SELECT current_mileage FROM fuel_tracker_history_table WHERE vehicle_id=:vehicleId ORDER BY id DESC LIMIT 1")
    suspend fun getLastKnownMileage(vehicleId: Int): Long?
}