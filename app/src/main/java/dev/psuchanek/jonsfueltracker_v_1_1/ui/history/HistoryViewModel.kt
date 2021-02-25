package dev.psuchanek.jonsfueltracker_v_1_1.ui.history

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import dev.psuchanek.jonsfueltracker_v_1_1.repositories.FuelTrackerRepository
import dev.psuchanek.jonsfueltracker_v_1_1.utils.Event

class HistoryViewModel @ViewModelInject constructor(private val repository: FuelTrackerRepository): ViewModel() {

    private val _forceFetch = MutableLiveData<Boolean>(false)

    private val _allTrips = _forceFetch.switchMap {
        repository.getAllTrips().asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val getAllTrips = _allTrips

    fun syncAllTrips() = _forceFetch.postValue(true)
}