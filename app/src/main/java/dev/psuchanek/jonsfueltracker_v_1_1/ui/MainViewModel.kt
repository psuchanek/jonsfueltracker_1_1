package dev.psuchanek.jonsfueltracker_v_1_1.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.asFuelTrackerTripModel
import dev.psuchanek.jonsfueltracker_v_1_1.repositories.FuelTrackerRepository
import dev.psuchanek.jonsfueltracker_v_1_1.utils.Event
import dev.psuchanek.jonsfueltracker_v_1_1.utils.TimePeriod
import dev.psuchanek.jonsfueltracker_v_1_1.utils.getTimePeriodTimestamp
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(private val repository: FuelTrackerRepository) :
    ViewModel() {

    private val _forceFetch = MutableLiveData<Boolean>(false)

    private val _tripsByTimestampRange = MutableLiveData<Event<List<FuelTrackerTrip>>>()
    val tripsByTimestampRange: LiveData<Event<List<FuelTrackerTrip>>> = _tripsByTimestampRange

//    private val _maintenanceTimestampRange = MutableLiveData<Event<List<Maintenance>>>()
//    val maintenanceTimestampRange: LiveData<Event<List<Maintenance>>> = _maintenanceTimestampRange


    val vehicleList = repository.observeAllVehicles

    private val _initialData = _forceFetch.switchMap {
        repository.getAllTrips().asLiveData(viewModelScope.coroutineContext)
    }.switchMap { resource ->
        MutableLiveData(Event(resource))
    }
    val fetchInitialData = _initialData

    val mostRecentTrip = repository.mostRecentTrip


    init {
        getTripsByTimestampRange(getTimePeriodTimestamp(TimePeriod.THREE_MONTHS))
    }

    fun fetchData() = _forceFetch.postValue(true)

    fun getTripsByTimestampRange(startTime: Long, endTime: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            try {
                val resultTripsList = repository.getAllByTimestampRange(startTime, endTime)
                if (resultTripsList.isNullOrEmpty()) {
                    _tripsByTimestampRange.postValue(Event(emptyList()))
                    return@launch
                }
                _tripsByTimestampRange.postValue(Event(resultTripsList.asFuelTrackerTripModel()))
            } catch (e: Exception) {
                _tripsByTimestampRange.postValue(Event(emptyList()))
            }

//            try {
//                val resultMaintenanceList =
//                    repository.getAllMaintenanceByTimestampRange(startTime, endTime)
//                if (resultMaintenanceList.isNullOrEmpty()) {
//                    _maintenanceTimestampRange.postValue(Event(emptyList()))
//                    return@launch
//                }
//                _maintenanceTimestampRange.postValue(Event(resultMaintenanceList))
//            } catch (e: Exception) {
//                _maintenanceTimestampRange.postValue(Event(emptyList()))
//            }
        }
    }

//    fun getMaintenanceByTimestampRange(
//        startTime: Long,
//        endTime: Long = System.currentTimeMillis()
//    ) {
//        viewModelScope.launch {
//            try {
//                val resultList = repository.getAllByTimestampRange(startTime, endTime)
//                if (resultList.isNullOrEmpty()) {
//                    _tripsByTimestampRange.postValue(Event(emptyList()))
//                    return@launch
//                }
//                _tripsByTimestampRange.postValue(Event(resultList.asFuelTrackerTripModel()))
//            } catch (e: Exception) {
//                _tripsByTimestampRange.postValue(Event(emptyList()))
//            }
//        }
//    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.job.isCancelled
    }
}