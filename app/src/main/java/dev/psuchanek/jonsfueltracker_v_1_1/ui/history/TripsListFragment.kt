package dev.psuchanek.jonsfueltracker_v_1_1.ui.history

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.psuchanek.jonsfueltracker_v_1_1.BaseFragment
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.adapters.FuelTrackerListAdapter
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.FragmentTripsBinding
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.asFuelTrackerTripModel
import dev.psuchanek.jonsfueltracker_v_1_1.utils.*

@AndroidEntryPoint
class TripsListFragment : BaseFragment(R.layout.fragment_trips) {


    private lateinit var binding: FragmentTripsBinding
    private val historyViewModel: HistoryViewModel by viewModels()
    private lateinit var listAdapter: FuelTrackerListAdapter<FuelTrackerTrip>
    private var deleteIcon: Drawable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentTripsBinding.inflate(inflater, container, false)
        deleteIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_trash, null)
        listAdapter = FuelTrackerListAdapter(TRIP_HISTORY, historyViewModel)
        subscribeObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRefresher()
        setupRecyclerView()
    }

    private fun setupRefresher() {
        binding.swipeRefresherTrips.setOnRefreshListener {
            subscribeSingleObserver()
            historyViewModel.syncAllTrips()

        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewHistory.apply {
            adapter = listAdapter
            addItemDecoration(CustomItemDecoration(DECORATION_SPACING))
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            ItemTouchHelper(itemTouchHelper).attachToRecyclerView(this)
        }
    }

    private val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {


        val background: ColorDrawable = ColorDrawable(Color.parseColor("#ba6b6c"))
        val backgroundCornerOffset = 20

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
            val itemView = viewHolder.itemView
            val iconMargin = (itemView.height - deleteIcon!!.intrinsicHeight) / 2
            val iconTop = itemView.top + (itemView.height - deleteIcon!!.intrinsicHeight) / 2
            val iconBottom = iconTop + deleteIcon!!.intrinsicHeight

            when {
                dX > 0 -> {
                    val iconLeft = itemView.left + iconMargin + deleteIcon!!.intrinsicWidth
                    val iconRight = itemView.left + iconMargin
                    deleteIcon!!.setBounds(iconRight, iconTop, iconLeft, iconBottom)

                    background.setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.left + (dX.toInt() + backgroundCornerOffset),
                        itemView.bottom
                    )
                }
                dX < 0 -> {
                    val iconLeft = itemView.right - iconMargin - deleteIcon!!.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    deleteIcon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                    background.setBounds(
                        itemView.right + (dX.toInt() - backgroundCornerOffset),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                }
                else -> background.setBounds(0, 0, 0, 0)
            }
            background.draw(c)
            deleteIcon!!.draw(c)
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
            val trip = listAdapter.currentList[position]
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
                    requireActivity().findViewById(R.id.rootLayout),
                    getString(R.string.trip_record_deleted),
                    Snackbar.LENGTH_LONG
                ).apply {
                    setActionTextColor(resources.getColor(R.color.secondaryDarkColor, null))
                    setAnchorView(R.id.bottomAppBar)
                    setAction(getString(R.string.undo)) {
                        undoDeleteTrip(trip)

                    }
                }.show()
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                listAdapter.notifyDataSetChanged()
                dialog.cancel()
            }
        dialogBuilder.show()

    }

    private fun undoDeleteTrip(trip: FuelTrackerTrip) {
        historyViewModel.insertTrip(trip)
        historyViewModel.deleteLocallyDeletedTripID(trip.id)
    }


    private fun subscribeSingleObserver() {
        historyViewModel.getAllTrips.observe(viewLifecycleOwner, Observer { event ->
            event?.let {
                val result = it.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        binding.swipeRefresherTrips.isRefreshing = false

                    }
                    Status.ERROR -> {
                        it.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { errorMessage ->
                                showSnackbar(errorMessage)
                            }
                        }

                        binding.swipeRefresherTrips.isRefreshing = false

                    }
                    Status.LOADING -> {
                        result.data?.let { trips ->
                            listAdapter.submitList(trips.asFuelTrackerTripModel())
                        }
                        binding.swipeRefresherTrips.isRefreshing = true
                    }
                }
            }

        })
    }

    private fun subscribeObservers() {
        historyViewModel.sortedTripHistory.observe(viewLifecycleOwner, Observer { sortedTripList ->

            listAdapter.apply {
                submitList(null)
                submitList(sortedTripList)
            }
        })
        historyViewModel.swipeLayout.observe(viewLifecycleOwner, Observer {
            binding.swipeRefresherTrips.isEnabled = !it
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.filter_trips_menu, menu)
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
                historyViewModel.sortTrips(SortType.PRICE_ASC)
                true
            }
            R.id.sortPriceHighToLow -> {
                historyViewModel.sortTrips(SortType.PRICE_DESC)
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