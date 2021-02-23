package dev.psuchanek.jonsfueltracker_v_1_1.ui.add

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.psuchanek.jonsfueltracker_v_1_1.repositories.FuelTrackerRepository
import dev.psuchanek.jonsfueltracker_v_1_1.utils.Status
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class AddTripViewModel @ViewModelInject constructor(private val repository: FuelTrackerRepository) :
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

                    _submitTripStatus.value = Status.SUCCESS
                } catch (e: IOException) {
                    Timber.d("submitTrip: SubmitFailed: ${e.printStackTrace()}")
                    _submitTripStatus.value = Status.ERROR
                }

            }

        }
    }


}