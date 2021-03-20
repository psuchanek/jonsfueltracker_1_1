package dev.psuchanek.jonsfueltracker_v_1_1.ui.add

import android.annotation.SuppressLint
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.Maintenance
import dev.psuchanek.jonsfueltracker_v_1_1.models.Vehicle
import dev.psuchanek.jonsfueltracker_v_1_1.repositories.Repository
import dev.psuchanek.jonsfueltracker_v_1_1.utils.EMPTY_MILEAGE
import dev.psuchanek.jonsfueltracker_v_1_1.utils.Status
import dev.psuchanek.jonsfueltracker_v_1_1.utils.capitalizeWords
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.util.*

@SuppressLint("DefaultLocale")
class AddViewModel @ViewModelInject constructor(private val repository: Repository) :
    ViewModel() {

    private var vehicleID: Int? = null

    private var _lastKnownMileage: MutableLiveData<Long> = MutableLiveData()
    val lastKnownMileage: LiveData<Long> = _lastKnownMileage

    private var _submitStatus: MutableLiveData<Status> = MutableLiveData()
    val submitStatus: LiveData<Status> = _submitStatus

    val observeAllVehicles = repository.observeAllVehicles


    fun getCurrentMileage(vehicleId: Int) {
        viewModelScope.launch {
            val currentMileage = repository.getLastKnownMileage(vehicleId)
            if (currentMileage != null) {
                _lastKnownMileage.postValue(currentMileage)
            } else {
                _lastKnownMileage.postValue(EMPTY_MILEAGE)
            }
        }
    }


    fun insertTrip(
        timestamp: Long,
        stationName: String,
        vehicleId: Int,
        price: String,
        ppl: String,
        fuelVolume: String,
        tripMileage: String,
        totalMileage: String

    ) {


        if ((stationName.isEmpty() || stationName == "") || (price.isEmpty() || price == "") || (tripMileage.isEmpty() || tripMileage == "")
            && (totalMileage.isEmpty() || totalMileage == "") || (ppl.isEmpty() || ppl == "") || (fuelVolume.isEmpty() || fuelVolume == "") || vehicleId < 1
        ) {
            _submitStatus.value = Status.ERROR
            return
        }
        GlobalScope.launch {
            try {
                repository.insertTrip(
                    FuelTrackerTrip(
                        vehicleId = vehicleId,
                        timestamp = timestamp,
                        fuelVolume = fuelVolume.toFloat(),
                        fuelCost = price.toFloat(),
                        tripMileage = tripMileage.toFloat(),
                        currentMileage = tripMileage.toInt(),
                        costPerLitre = ppl.toFloat(),
                        gasStationName = stationName.toLowerCase(Locale.ROOT).capitalize()
                    )
                )
                _submitStatus.postValue(Status.SUCCESS)
            } catch (e: IOException) {
                Timber.d("submitTrip: SubmitFailed: ${e.printStackTrace()}")
                _submitStatus.postValue(Status.ERROR)
            }


        }
    }

    fun insertVehicle(vehicleName: String) {
        if (vehicleName.isNullOrEmpty()) {
            _submitStatus.value = Status.ERROR
            return
        }
        GlobalScope.launch {
            try {
                repository.insertVehicle(
                    vehicle = Vehicle(
                        vehicleName = vehicleName.capitalizeWords()
                    )
                )
                _submitStatus.postValue(Status.SUCCESS)
            } catch (e: IOException) {
                _submitStatus.postValue(Status.ERROR)
            }

        }
    }

    fun insertMaintenance(
        timestamp: Long,
        vehicleID: Int,
        workshopName: String,
        workDoneDescription: String,
        workPrice: String,
        currentMileage: Int
    ) {
        if ((workshopName.isEmpty() || workshopName == "") || (workDoneDescription.isEmpty() || workDoneDescription == "") ||
            (workPrice.isEmpty() || workPrice == "") || vehicleID < 1
        ) {
            _submitStatus.value = Status.ERROR
            return
        }
        GlobalScope.launch {
            try {
                repository.insertMaintenance(
                    Maintenance(
                        timestamp = timestamp,
                        vehicleID = vehicleID,
                        workshopName = workshopName.toLowerCase(Locale.ROOT).capitalize(),
                        workDoneDescription = workDoneDescription.toLowerCase(Locale.ROOT)
                            .capitalize(),
                        workPrice = workPrice.toFloat(),
                        currentMileage = currentMileage
                    )
                )
                _submitStatus.postValue(Status.SUCCESS)


            } catch (e: IOException) {
                _submitStatus.postValue(Status.ERROR)
            }

        }

    }
}