package dev.psuchanek.jonsfueltracker_v_1_1.ui.history

import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip

class OnTripClickListener(val listener: (trip: FuelTrackerTrip) -> Unit) {
    fun onClick(trip: FuelTrackerTrip) = listener(trip)
}