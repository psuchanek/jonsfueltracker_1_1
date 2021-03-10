package dev.psuchanek.jonsfueltracker_v_1_1.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.LocalFuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.asFuelTrackerTripModel
import dev.psuchanek.jonsfueltracker_v_1_1.repositories.FuelTrackerRepository
import dev.psuchanek.jonsfueltracker_v_1_1.utils.Event
import dev.psuchanek.jonsfueltracker_v_1_1.utils.TimePeriod
import dev.psuchanek.jonsfueltracker_v_1_1.utils.getTimePeriodTimestamp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(private val repository: FuelTrackerRepository) :
    ViewModel() {

    private val _tripsByTimestampRange = MutableLiveData<Event<List<FuelTrackerTrip>>>()
    val tripsByTimestampRange: LiveData<Event<List<FuelTrackerTrip>>> = _tripsByTimestampRange

    val mostRecentTrip: LiveData<LocalFuelTrackerTrip>? = repository.getMostRecentTripRecord()

    val vehicleList = repository.observeAllVehicles

    init {
        GlobalScope.launch { repository.getAllTrips() }
        getTripsByTimestampRange(getTimePeriodTimestamp(TimePeriod.THREE_MONTHS))
    }

    fun getTripsByTimestampRange(startTime: Long, endTime: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            try {
                val resultList = repository.getAllByTimestampRange(startTime, endTime)
                if (resultList.isNullOrEmpty()) {
                    _tripsByTimestampRange.postValue(Event(emptyList()))
                    return@launch
                }
                _tripsByTimestampRange.postValue(Event(resultList.asFuelTrackerTripModel()))
            } catch (e: Exception) {
                _tripsByTimestampRange.postValue(Event(emptyList()))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.job.isCancelled
    }
}