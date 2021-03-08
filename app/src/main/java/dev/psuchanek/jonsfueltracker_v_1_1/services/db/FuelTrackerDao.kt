package dev.psuchanek.jonsfueltracker_v_1_1.services.db

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.psuchanek.jonsfueltracker_v_1_1.models.LocalFuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.LocallyDeletedTrip
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelTrackerDao {

    /*
    Insert calls to database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelTrackerHistory(fuelTrackerHistory: List<LocalFuelTrackerTrip>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelTrackerTrip(trip: LocalFuelTrackerTrip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocallyDeletedTripId(deletedTripID: LocallyDeletedTrip)

    /*
    Update trip
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateFuelTrackerTrip(trip: LocalFuelTrackerTrip)

    /*
   Delete calls to database
    */
    @Query("DELETE FROM fuel_tracker_history_table WHERE id = :tripID")
    suspend fun deleteTripByID(tripID: String)

    @Query("DELETE FROM locally_deleted_trips_ids WHERE deleteTripID = :tripID")
    suspend fun deleteLocallyDeletedTripId(tripID: String)

    @Query("DELETE FROM fuel_tracker_history_table")
    suspend fun deleteAllTrips()


    /*
      Get calls to database
       */
    @Query("SELECT * FROM fuel_tracker_history_table ORDER BY id DESC")
    fun getAllById(): List<LocalFuelTrackerTrip>

    @Query("SELECT * FROM fuel_tracker_history_table ORDER BY timestamp DESC")
    fun getAllByTimestamp(): Flow<List<LocalFuelTrackerTrip>>

    @Query("SELECT * FROM fuel_tracker_history_table WHERE is_synced = 0")
    suspend fun getAllUnsyncedTrips(): List<LocalFuelTrackerTrip>

    @Query("SELECT * FROM fuel_tracker_history_table WHERE id = :tripId")
    fun observerTripById(tripId: Int): LiveData<LocalFuelTrackerTrip>?

    @Query("SELECT * FROM fuel_tracker_history_table WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    suspend fun getAllByTimestampRange(start: Long, end: Long): List<LocalFuelTrackerTrip>?

    //TODO: Focus on the rest here after refactoring

    @Query("SELECT * FROM fuel_tracker_history_table ORDER BY fuel_volume DESC")
    fun getAllByFuelVolume(): List<LocalFuelTrackerTrip>

    @Query("SELECT * FROM fuel_tracker_history_table ORDER BY fuel_cost DESC")
    fun getAllByFuelCost(): List<LocalFuelTrackerTrip>

    @Query("SELECT * FROM fuel_tracker_history_table ORDER BY trip_mileage DESC")
    fun getAllByTripMileage(): List<LocalFuelTrackerTrip>

    @Query("SELECT * FROM fuel_tracker_history_table ORDER by timestamp DESC LIMIT 1")
    fun getMostRecentTripRecord(): LiveData<LocalFuelTrackerTrip>?

    @Query("SELECT current_mileage FROM fuel_tracker_history_table WHERE vehicle_id=:vehicleId ORDER BY id DESC LIMIT 1")
    suspend fun getLastKnownMileage(vehicleId: Int): Long?

    @Query("SELECT * FROM locally_deleted_trips_ids")
    suspend fun getAllLocallyDeletedTripIDs(): List<LocallyDeletedTrip>
}