package dev.psuchanek.jonsfueltracker_v_1_1.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.asFuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.asFuelTrackerTripModel
import dev.psuchanek.jonsfueltracker_v_1_1.repositories.FuelTrackerRepository
import dev.psuchanek.jonsfueltracker_v_1_1.utils.Event
import dev.psuchanek.jonsfueltracker_v_1_1.utils.TimePeriod
import dev.psuchanek.jonsfueltracker_v_1_1.utils.getTimePeriodTimestamp
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel @ViewModelInject constructor(private val repository: FuelTrackerRepository) :
    ViewModel() {

    private val _tripsByTimestampRange = MutableLiveData<Event<List<FuelTrackerTrip>>>()
    val tripsByTimestampRange: LiveData<Event<List<FuelTrackerTrip>>> = _tripsByTimestampRange

    val mostRecentTrip: LiveData<FuelTrackerTrip>? = repository.getMostRecentTripRecord()?.map {
        it.asFuelTrackerTrip()
    }

    init {
        Timber.d("DEBUG: inside init mainViewModel")
        getTripsByTimestampRange(getTimePeriodTimestamp(TimePeriod.THREE_MONTHS))
    }

    fun getTripsByTimestampRange(startTime: Long, endTime: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            try {
                val resultList = repository.getAllByTimestampRange(startTime, endTime)
                if(resultList.isNullOrEmpty()) {
                    _tripsByTimestampRange.postValue(Event(emptyList()))
                    return@launch
                }
                _tripsByTimestampRange.postValue(Event(resultList.asFuelTrackerTripModel()))
            }catch (e: Exception) {
                _tripsByTimestampRange.postValue(Event(emptyList()))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.job.isCancelled
    }
}