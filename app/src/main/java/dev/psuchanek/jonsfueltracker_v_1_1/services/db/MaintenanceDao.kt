package dev.psuchanek.jonsfueltracker_v_1_1.services.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.psuchanek.jonsfueltracker_v_1_1.models.Maintenance

@Dao
interface MaintenanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenance(maintenance: Maintenance)

    @Query("DELETE FROM maintenance_table WHERE maintenance_id = :maintenanceID")
    suspend fun deleteTripByID(maintenanceID: String)

    @Query("SELECT * FROM maintenance_table")
    fun getAllMaintenance(): List<Maintenance>

    @Query("SELECT * FROM maintenance_table WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    suspend fun getAllMaintenanceByTimestampRange(start: Long, end: Long): List<Maintenance>?

    @Query("SELECT * FROM maintenance_table")
    fun observeAllMaintenance(): LiveData<List<Maintenance>>

    @Query("SELECT * FROM maintenance_table ORDER BY timestamp DESC")
    fun observeAllMaintenanceByTimestamp(): LiveData<List<Maintenance>>

    @Query("SELECT * FROM maintenance_table ORDER BY price DESC")
    fun observeAllMaintenanceByPrice(): LiveData<List<Maintenance>>
}