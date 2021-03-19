package dev.psuchanek.jonsfueltracker_v_1_1.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dev.psuchanek.jonsfueltracker_v_1_1.BaseFragment
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.adapters.AddCollectionAdapter
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.FragmentAddBinding

class AddFragment : BaseFragment(R.layout.fragment_add) {
    private lateinit var binding: FragmentAddBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var addCollectionAdapter: AddCollectionAdapter
    private lateinit var addTabLayout: TabLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = binding.viewPagerAdd
        addCollectionAdapter = AddCollectionAdapter(this)
        viewPager.adapter = addCollectionAdapter
        addTabLayout = binding.tabLayoutAdd
        TabLayoutMediator(addTabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "TRIP"
                }
                1 -> {
                    tab.text = "MAINTENANCE"
                }
                2 -> {
                    tab.text = "VEHICLE"
                }
            }
        }.attach()

    }
}