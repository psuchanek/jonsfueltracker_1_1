package dev.psuchanek.jonsfueltracker_v_1_1.ui.history

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dev.psuchanek.jonsfueltracker_v_1_1.repositories.FuelTrackerRepository

class HistoryViewModel @ViewModelInject constructor(private val repository: FuelTrackerRepository): ViewModel() {
}