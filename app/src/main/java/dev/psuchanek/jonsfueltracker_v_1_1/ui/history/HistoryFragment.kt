package dev.psuchanek.jonsfueltracker_v_1_1.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.psuchanek.jonsfueltracker_v_1_1.BaseFragment
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.adapters.TripHistoryAdapter
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.FragmentHistoryBinding
import dev.psuchanek.jonsfueltracker_v_1_1.utils.CustomItemDecoration

@AndroidEntryPoint
class HistoryFragment : BaseFragment(R.layout.fragment_history) {

    private lateinit var binding: FragmentHistoryBinding
    private val historyViewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        subscribeObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRefresher()
        setupSpinner()
        setupRecyclerView()
    }

    private fun setupRefresher() {
        binding.swipeRefresher.setOnRefreshListener {

        }
    }

    private fun setupRecyclerView() {
        val historyAdapter = TripHistoryAdapter(OnTripClickListener { })
        binding.recyclerViewHistory.apply {
            adapter = historyAdapter
            addItemDecoration(CustomItemDecoration(15))
        }
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_by,
            android.R.layout.simple_spinner_item
        ).also {
            binding.spinnerSort.adapter = it
        }
        //TODO: add selection visible depending on SortType
        binding.spinnerSort.onItemSelectedListener = onItemSelectedListener()
    }

    private fun onItemSelectedListener(): AdapterView.OnItemSelectedListener? =
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

        }

    private fun subscribeObservers() {

    }
}