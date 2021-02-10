package dev.psuchanek.jonsfueltracker_v_1_1.ui.add

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.LocalFuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.asDatabaseModel
import dev.psuchanek.jonsfueltracker_v_1_1.other.*
import dev.psuchanek.jonsfueltracker_v_1_1.repositories.FuelTrackerRepository
import dev.psuchanek.jonsfueltracker_v_1_1.repositories.Repository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.lang.IndexOutOfBoundsException

class AddTripViewModel @ViewModelInject constructor(private val repository: Repository) :
    ViewModel() {

    private var _lastTripId: Int = 0

    private var _lastKnownMileage: MutableLiveData<Long> = MutableLiveData()
    val lastKnownMileage: LiveData<Long> = _lastKnownMileage

    private var _submitTripStatus: MutableLiveData<Status> = MutableLiveData()
    val submitTripStatus: LiveData<Status> = _submitTripStatus


    //TODO: Implement logic in init{} to get lastTripId even when DB is empty
    fun getLastTripId() {
        viewModelScope.launch {
            var id = 0
            try {
                val response = repository.getTripsSortedById()[0].id
            } catch (e: IndexOutOfBoundsException) {

            }
        }

    }


    fun getCurrentMileage(vehicleId: Int) {
        var response: Long = 0L
        viewModelScope.launch {
            try {
                response = repository.getLastKnownMileage(vehicleId)
                _lastKnownMileage.value = response
            } catch (e: NullPointerException) {
                Timber.d("${e.printStackTrace()}")
                _lastKnownMileage.value = 0L
            }

        }

    }

    fun submitTrip(
        date: String,
        stationName: String,
        vehicleId: Int,
        price: String,
        ppl: String,
        fuelVolume: String,
        tripMileage: String,
        totalMileage: String

    ) {
        if ((date.isEmpty() || date == "") || (stationName.isEmpty() || stationName == "") || (price.isEmpty() || price == "") || (tripMileage.isEmpty() || tripMileage == "")
            && (totalMileage.isEmpty() || totalMileage == "") || (ppl.isEmpty() || ppl == "") || (fuelVolume.isEmpty() || fuelVolume == "") || (vehicleId < 0 || vehicleId > 2)
        ) {
            _submitTripStatus.value = Status.ERROR
        } else {
            viewModelScope.launch {
                try {
                    repository.insertFuelTrackerTrip(
                        FuelTrackerTrip(
                            id = _lastTripId,
                            vehicleId = vehicleId,
                            timestamp = date.convertToTimeInMillis(),
                            fuelVolume = fuelVolume.toFloat(),
                            fuelCost = price.toFloat(),
                            tripMileage = tripMileage.toFloat(),
                            currentMileage = totalMileage.toInt(),
                            costPerLitre = ppl.toFloat(),
                            gasStationName = stationName
                        )
                    )
                    _submitTripStatus.value = Status.SUCCESS
                } catch (e: IOException) {
                    Timber.d("submitTrip: SubmitFailed: ${e.printStackTrace()}")
                    _submitTripStatus.value = Status.EXCEPTION
                }

            }

        }
    }


}