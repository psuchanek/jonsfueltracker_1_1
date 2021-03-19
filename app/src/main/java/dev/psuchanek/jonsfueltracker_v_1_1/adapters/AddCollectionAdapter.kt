package dev.psuchanek.jonsfueltracker_v_1_1.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.psuchanek.jonsfueltracker_v_1_1.ui.add.AddMaintenanceFragment
import dev.psuchanek.jonsfueltracker_v_1_1.ui.add.AddTripFragment
import dev.psuchanek.jonsfueltracker_v_1_1.ui.add.AddVehicleFragment
import dev.psuchanek.jonsfueltracker_v_1_1.utils.FRAGMENT_ADD_TAB_COUNT

class AddCollectionAdapter(fm: Fragment) : FragmentStateAdapter(fm) {
    override fun getItemCount() = FRAGMENT_ADD_TAB_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                AddTripFragment()
            }
            1 -> {
                AddMaintenanceFragment()
            }
            2 -> {
                AddVehicleFragment()
            }
            else -> throw Exception("Unknown fragment passed into adapter")
        }
    }
}