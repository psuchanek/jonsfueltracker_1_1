package dev.psuchanek.jonsfueltracker_v_1_1.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import dev.psuchanek.jonsfueltracker_v_1_1.BaseFragment
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.adapters.TripHistoryAdapter
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.FragmentHistoryBinding
import dev.psuchanek.jonsfueltracker_v_1_1.models.asFuelTrackerTripModel
import dev.psuchanek.jonsfueltracker_v_1_1.utils.CustomItemDecoration
import dev.psuchanek.jonsfueltracker_v_1_1.utils.Status

@AndroidEntryPoint
class HistoryFragment : BaseFragment(R.layout.fragment_history) {

    private lateinit var binding: FragmentHistoryBinding
    private val historyViewModel: HistoryViewModel by viewModels()
    private lateinit var tripAdapter: TripHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        tripAdapter = TripHistoryAdapter((OnTripClickListener { }))
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
            historyViewModel.syncAllTrips()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewHistory.apply {
            adapter = tripAdapter
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
        historyViewModel.getAllTrips.observe(viewLifecycleOwner, Observer { event ->
            event?.let {
                val result = it.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        tripAdapter.submitList(result.data!!.asFuelTrackerTripModel())
                        binding.swipeRefresher.isRefreshing = false

                    }
                    Status.ERROR -> {
                        it.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { errorMessage ->
                                showSnackbar(errorMessage)
                            }
                        }

                        binding.swipeRefresher.isRefreshing = false

                    }
                    Status.LOADING -> {
                        result.data?.let { trips ->
                            tripAdapter.submitList(trips.asFuelTrackerTripModel())
                        }
                        binding.swipeRefresher.isRefreshing = true
                    }
                }
            }

        })
    }
}