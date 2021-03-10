package dev.psuchanek.jonsfueltracker_v_1_1.services.db

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.psuchanek.jonsfueltracker_v_1_1.models.Vehicle

@Dao
interface VehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicleList: List<Vehicle>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: Vehicle)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateVehicleList(vehicleList: List<Vehicle>)

    @Query("SELECT * FROM vehicle_table")
    fun getAllVehicles(): List<Vehicle>

    @Query("SELECT * FROM vehicle_table")
    fun observeAllVehicles(): LiveData<List<Vehicle>>
}