package dev.psuchanek.jonsfueltracker_v_1_1.ui.history

import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip

interface OnTripClickListener {
    fun onClick(trip: FuelTrackerTrip, position: Int)

}