package dev.psuchanek.jonsfueltracker_v_1_1.ui.history

import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
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
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.FragmentHistoryBinding
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.asFuelTrackerTripModel
import dev.psuchanek.jonsfueltracker_v_1_1.utils.CustomItemDecoration
import dev.psuchanek.jonsfueltracker_v_1_1.utils.DECORATION_SPACING
import dev.psuchanek.jonsfueltracker_v_1_1.utils.SortType
import dev.psuchanek.jonsfueltracker_v_1_1.utils.Status

@AndroidEntryPoint
class HistoryFragment : BaseFragment(R.layout.fragment_history) {

    //TODO: fix animation arrow recycling behavior and implement sorting function

    private lateinit var binding: FragmentHistoryBinding
    private val historyViewModel: HistoryViewModel by viewModels()
    private lateinit var tripAdapter: FuelTrackerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        tripAdapter = FuelTrackerAdapter()
        subscribeObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRefresher()
        setupRecyclerView()
    }

    private fun setupRefresher() {
        binding.swipeRefresherHistory.isRefreshing = false
        binding.swipeRefresherHistory.setOnRefreshListener {
            subscribeSingleObserver()
            historyViewModel.syncAllTrips()

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
            showDeleteDialog(trip)
        }
    }

    private fun showDeleteDialog(trip: FuelTrackerTrip) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.delete_alert_dialog_message))
            .setPositiveButton(
                "YES"
            ) { _, _ ->
                historyViewModel.deleteTrip(trip.id)
                Snackbar.make(
                    requireView(),
                    getString(R.string.trip_record_deleted),
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction(getString(R.string.undo)) {
                        undoDeleteTrip(trip)
                    }
                }.show()
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                tripAdapter.notifyDataSetChanged()
                dialog.cancel()
            }
        dialogBuilder.show()

    }

    private fun undoDeleteTrip(trip: FuelTrackerTrip) {
        historyViewModel.insertTrip(trip)
        historyViewModel.deleteLocallyDeletedTripID(trip.id)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewHistory.apply {
            adapter = tripAdapter
            addItemDecoration(CustomItemDecoration(DECORATION_SPACING))
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            ItemTouchHelper(itemTouchHelper).attachToRecyclerView(this)
        }
    }

    private fun subscribeSingleObserver() {
        historyViewModel.getAllTrips.observe(viewLifecycleOwner, Observer { event ->
            event?.let {
                val result = it.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        tripAdapter.submitList(
                            result.data!!.asFuelTrackerTripModel()
                        )
                        tripAdapter.notifyDataSetChanged()
                        binding.swipeRefresherHistory.isRefreshing = false

                    }
                    Status.ERROR -> {
                        it.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { errorMessage ->
                                showSnackbar(errorMessage)
                            }
                        }

                        binding.swipeRefresherHistory.isRefreshing = false

                    }
                    Status.LOADING -> {
                        result.data?.let { trips ->
                            tripAdapter.submitList(trips.asFuelTrackerTripModel())
                        }
                        binding.swipeRefresherHistory.isRefreshing = true
                    }
                }
            }

        })
}

    private fun subscribeObservers() {
        historyViewModel.sortedTripHistory.observe(viewLifecycleOwner, Observer { sortedTripList ->
            tripAdapter.submitList(sortedTripList)


        })

        historyViewModel.swipeLayout.observe(viewLifecycleOwner, Observer {
            binding.swipeRefresherHistory.isEnabled = !it
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.filter_menu, menu)
        inflater.inflate(R.menu.settings_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sortDateNewToOld -> {
                historyViewModel.sortTrips(SortType.DATE_DESC)
                true
            }
            R.id.sortDateOldToNew -> {
                historyViewModel.sortTrips(SortType.DATE_ASC)
                true
            }
            R.id.sortPriceLowToHigh -> {
                historyViewModel.sortTrips(SortType.FILL_PRICE_ASC)
                true
            }
            R.id.sortPriceHighToLow -> {
                historyViewModel.sortTrips(SortType.FILL_PRICE_DESC)
                true
            }
            R.id.sortMileageLowToHigh -> {
                historyViewModel.sortTrips(SortType.TRIP_MILEAGE_ASC)
                true
            }
            R.id.sortMileageHighToLow -> {
                historyViewModel.sortTrips(SortType.TRIP_MILEAGE_DESC)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}