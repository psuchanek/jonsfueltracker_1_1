package dev.psuchanek.jonsfueltracker_v_1_1.ui.history

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.psuchanek.jonsfueltracker_v_1_1.BaseFragment
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.adapters.FuelTrackerAdapter
import dev.psuchanek.jonsfueltracker_v_1_1.adapters.animations.ArrowAnimation
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.FragmentHistoryBinding
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.asFuelTrackerTripModel
import dev.psuchanek.jonsfueltracker_v_1_1.utils.*
import kotlinx.android.synthetic.main.fragment_history.view.*

@AndroidEntryPoint
class HistoryFragment : BaseFragment(R.layout.fragment_history), OnTripClickListener {

    //TODO: fix animation arrow recycling behavior and implement sorting function

    private lateinit var binding: FragmentHistoryBinding
    private val historyViewModel: HistoryViewModel by viewModels()
    private lateinit var tripAdapter: FuelTrackerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        tripAdapter = FuelTrackerAdapter(this)
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
            tripAdapter.notifyDataSetChanged()
            tripAdapter.currentList.onEach { trip ->
                trip.isExpanded = false

            }
            tripAdapter.notifyDataSetChanged()
        }
    }

    private val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                historyViewModel.swipeLayoutActive(isCurrentlyActive)
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val trip = tripAdapter.currentList[position]
            historyViewModel.deleteTrip(trip.id)
            Snackbar.make(
                requireView(),
                getString(R.string.trip_record_deleted),
                Snackbar.LENGTH_LONG
            ).apply {
                setAction(getString(R.string.undo)) {
                    historyViewModel.insertTrip(trip)
                    historyViewModel.deleteLocallyDeletedTripID(trip.id)
                }
            }.show()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewHistory.apply {
            adapter = tripAdapter
            addItemDecoration(CustomItemDecoration(DECORATION_SPACING))
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            ItemTouchHelper(itemTouchHelper).attachToRecyclerView(this)
            itemAnimator = ArrowAnimation()
        }
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_by,
            android.R.layout.simple_spinner_item
        ).also {
            binding.dropdownSort.setAdapter(it)
        }
        binding.dropdownSort.apply {
            when (historyViewModel.sortType) {
                SortType.DATE_DESC -> dropdownSort.setSelection(0)
                SortType.DATE_ASC -> dropdownSort.setSelection(1)
                SortType.FILL_PRICE_DESC -> dropdownSort.setSelection(2)
                SortType.FILL_PRICE_ASC -> dropdownSort.setSelection(3)
                SortType.TRIP_MILEAGE_DESC -> dropdownSort.setSelection(4)
                SortType.TRIP_MILEAGE_ASC -> dropdownSort.setSelection(5)
            }
            onItemSelectedListener = onItemSelectedListener()
        }
    }

    private fun onItemSelectedListener(): AdapterView.OnItemSelectedListener? =
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        historyViewModel.sortTrips(SortType.DATE_DESC)
                    }

                    1 -> {
                        historyViewModel.sortTrips(SortType.DATE_ASC)
                    }
                    2 -> {
                        historyViewModel.sortTrips(SortType.FILL_PRICE_DESC)
                    }

                    3 -> {
                        historyViewModel.sortTrips(SortType.FILL_PRICE_ASC)
                    }
                    4 -> {
                        historyViewModel.sortTrips(SortType.TRIP_MILEAGE_DESC)
                    }

                    5 -> {
                        historyViewModel.sortTrips(SortType.TRIP_MILEAGE_ASC)
                    }
                }
            }

        }


    private fun subscribeObservers() {
        historyViewModel.getAllTrips.observe(viewLifecycleOwner, Observer { event ->
            event?.let {
                val result = it.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        tripAdapter.submitList(
                            result.data!!.asFuelTrackerTripModel()
                        )
                        tripAdapter.notifyDataSetChanged()
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

        historyViewModel.sortedTripHistory.observe(viewLifecycleOwner, Observer { event ->
            event?.let {
                val result = it.peekContent()
                tripAdapter.submitList(result)
                tripAdapter.notifyDataSetChanged()
            }

        })

        historyViewModel.swipeLayout.observe(viewLifecycleOwner, Observer {
            binding.swipeRefresher.isEnabled = !it
        })
    }

    override fun onClick(trip: FuelTrackerTrip, position: Int) {
        trip.isExpanded = !trip.isExpanded
        if (!trip.isExpanded) {
            tripAdapter.notifyItemChanged(position, ARROW_ANIM_DOWN)
        } else {
            tripAdapter.notifyItemChanged(position, ARROW_ANIM_UP)
        }

    }
}