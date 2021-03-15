package dev.psuchanek.jonsfueltracker_v_1_1.ui.history

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.asFuelTrackerTripModel
import dev.psuchanek.jonsfueltracker_v_1_1.repositories.FuelTrackerRepository
import dev.psuchanek.jonsfueltracker_v_1_1.utils.Event
import dev.psuchanek.jonsfueltracker_v_1_1.utils.SortType
import kotlinx.coroutines.launch
import timber.log.Timber

class HistoryViewModel @ViewModelInject constructor(private val repository: FuelTrackerRepository) :
    ViewModel() {

    private val _forceFetch = MutableLiveData<Boolean>(false)
    private var _sortType = SortType.DATE_DESC

    private val _swipeLayout = MutableLiveData<Boolean>()
    val swipeLayout: LiveData<Boolean> = _swipeLayout

    /**
    Sorting sources
     */
    private val tripsSortedByDate = repository.observeAllByTimestamp
    private val tripsSortedByFuelCost = repository.observeAllByFuelCost
    private val tripsSortedByTripMileage = repository.observeAllByTripMileage


    val sortedTripHistory = MediatorLiveData<List<FuelTrackerTrip>>()


    /**
     * fetches all trips from database and syncs data with service
     */
    private val _allTrips = _forceFetch.switchMap {
        repository.getAllTrips().asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val getAllTrips = _allTrips

    init {
        addSources()
    }


    fun syncAllTrips() = _forceFetch.postValue(true)

    private fun addSources() {
        sortedTripHistory.addSource(tripsSortedByDate) { result ->
            result?.let {
                if (_sortType == SortType.DATE_ASC) {
                    sortedTripHistory.value = it.asFuelTrackerTripModel().reversed()
                }
                if (_sortType == SortType.DATE_DESC) {
                    sortedTripHistory.value = it.asFuelTrackerTripModel()
                }
            }

        }

        sortedTripHistory.addSource(tripsSortedByFuelCost) { result ->
            result?.let {
                if (_sortType == SortType.FILL_PRICE_ASC) {
                    sortedTripHistory.postValue(it.asFuelTrackerTripModel().reversed())
                }
                if (_sortType == SortType.FILL_PRICE_DESC) {
                    sortedTripHistory.postValue(it.asFuelTrackerTripModel())
                }
            }

        }

        sortedTripHistory.addSource(tripsSortedByTripMileage) { result ->
            result?.let {
                if (_sortType == SortType.TRIP_MILEAGE_ASC) {
                    sortedTripHistory.postValue(it.asFuelTrackerTripModel().reversed())
                }
                if (_sortType == SortType.TRIP_MILEAGE_DESC) {
                    sortedTripHistory.postValue(it.asFuelTrackerTripModel())
                }
            }

        }


    }

    fun sortTrips(sortType: SortType) {
        Timber.d("DEBUG: sorting got called with sort type: $sortType")
        when (sortType) {
            SortType.DATE_ASC -> {
                tripsSortedByDate.value?.let {
                    sortedTripHistory.value =it.asFuelTrackerTripModel().reversed()

                }
            }
            SortType.DATE_DESC -> {
                tripsSortedByDate.value?.let {
                    sortedTripHistory.value = it.asFuelTrackerTripModel()
                }
            }
            SortType.TRIP_MILEAGE_ASC -> {
                tripsSortedByTripMileage.value?.let {
                    sortedTripHistory.value = it.asFuelTrackerTripModel().reversed()
                }
            }
            SortType.TRIP_MILEAGE_DESC -> {
                tripsSortedByTripMileage.value?.let {
                    sortedTripHistory.value = it.asFuelTrackerTripModel()
                }
            }
            SortType.FILL_PRICE_ASC -> {
                tripsSortedByFuelCost.value?.let {
                    sortedTripHistory.value = it.asFuelTrackerTripModel().reversed()
                }
            }
            SortType.FILL_PRICE_DESC -> {
                tripsSortedByFuelCost.value?.let {
                    sortedTripHistory.value = it.asFuelTrackerTripModel()
                }
            }
        }.also {
            this._sortType = sortType
        }
    }

    fun deleteTrip(tripID: String) =
        viewModelScope.launch {
            repository.deleteTripByID(tripID)
        }


    fun insertTrip(trip: FuelTrackerTrip) =
        viewModelScope.launch {
            repository.insertTrip(trip)
        }


    fun deleteLocallyDeletedTripID(tripID: String) =
        viewModelScope.launch {
            repository.deleteLocallyDeletedTripID(tripID)
        }

    fun swipeLayoutActive(isCurrentlyActive: Boolean) {
        _swipeLayout.value = isCurrentlyActive
    }



}