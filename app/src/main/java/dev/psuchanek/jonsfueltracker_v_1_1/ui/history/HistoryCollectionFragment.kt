package dev.psuchanek.jonsfueltracker_v_1_1.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dev.psuchanek.jonsfueltracker_v_1_1.BaseFragment
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.adapters.HistoryCollectionAdapter
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.FragmentHistoryCollectionBinding


class HistoryCollectionFragment : BaseFragment(R.layout.fragment_history_collection) {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var historyCollectionAdapter: HistoryCollectionAdapter
    private lateinit var binding: FragmentHistoryCollectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = binding.viewPagerHistory
        tabLayout = binding.tabLayoutHistory
        historyCollectionAdapter = HistoryCollectionAdapter(this)
        viewPager.adapter = historyCollectionAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = resources.getString(R.string.trips_tab_label)
                }
                1 -> {
                    tab.text = resources.getString(R.string.maintenance_tab_label)
                }
            }
        }.attach()
    }
}