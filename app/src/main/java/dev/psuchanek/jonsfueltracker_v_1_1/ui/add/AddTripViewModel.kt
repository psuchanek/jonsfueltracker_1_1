package dev.psuchanek.jonsfueltracker_v_1_1.ui.add

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.repositories.Repository
import dev.psuchanek.jonsfueltracker_v_1_1.utils.EMPTY_MILEAGE
import dev.psuchanek.jonsfueltracker_v_1_1.utils.Status
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class AddTripViewModel @ViewModelInject constructor(private val repository: Repository) :
    ViewModel() {

    private var vehicleID: Int? = null

    private var _lastKnownMileage: MutableLiveData<Long> = MutableLiveData()
    val lastKnownMileage: LiveData<Long> = _lastKnownMileage

    private var _submitTripStatus: MutableLiveData<Status> = MutableLiveData()
    val submitTripStatus: LiveData<Status> = _submitTripStatus


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
            && (totalMileage.isEmpty() || totalMileage == "") || (ppl.isEmpty() || ppl == "") || (fuelVolume.isEmpty() || fuelVolume == "") || (vehicleId < 1 || vehicleId > 3)
        ) {
            _submitTripStatus.value = Status.ERROR
        } else {
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
                            gasStationName = stationName
                        )
                    )
                    _submitTripStatus.postValue(Status.SUCCESS)
                } catch (e: IOException) {
                    Timber.d("submitTrip: SubmitFailed: ${e.printStackTrace()}")
                    _submitTripStatus.postValue(Status.ERROR)
                }

            }

        }
    }
    }