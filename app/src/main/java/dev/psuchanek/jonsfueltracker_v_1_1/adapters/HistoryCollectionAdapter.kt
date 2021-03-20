package dev.psuchanek.jonsfueltracker_v_1_1.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.psuchanek.jonsfueltracker_v_1_1.ui.history.TripsListFragment
import dev.psuchanek.jonsfueltracker_v_1_1.ui.history.MaintenanceListFragment
import dev.psuchanek.jonsfueltracker_v_1_1.utils.FRAGMENT_HISTORY_TAB_COUNT

class HistoryCollectionAdapter(fm: Fragment): FragmentStateAdapter(fm) {
    override fun getItemCount() = FRAGMENT_HISTORY_TAB_COUNT

    override fun createFragment(position: Int): Fragment {
       return when(position) {
           0 -> {
               TripsListFragment()
           }
           1 -> {
               MaintenanceListFragment()
           }
           else -> throw Exception("Unknown fragment passed into adapter")
       }
    }
}