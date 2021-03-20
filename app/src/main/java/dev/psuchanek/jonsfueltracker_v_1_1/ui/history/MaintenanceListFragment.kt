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
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.FragmentMaintenaceBinding
import dev.psuchanek.jonsfueltracker_v_1_1.models.Maintenance
import dev.psuchanek.jonsfueltracker_v_1_1.utils.CustomItemDecoration
import dev.psuchanek.jonsfueltracker_v_1_1.utils.DECORATION_SPACING
import dev.psuchanek.jonsfueltracker_v_1_1.utils.MAINTENANCE
import dev.psuchanek.jonsfueltracker_v_1_1.utils.SortType

@AndroidEntryPoint
class MaintenanceListFragment : BaseFragment(R.layout.fragment_maintenace) {

    private val historyViewModel: HistoryViewModel by viewModels()

    private lateinit var binding: FragmentMaintenaceBinding
    private lateinit var listAdapter: FuelTrackerListAdapter<Maintenance>
    private var deleteIcon: Drawable? = null
//    private lateinit var itemTouchHelper: ItemTouchHelper.SimpleCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentMaintenaceBinding.inflate(inflater, container, false)
        deleteIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_trash, null)
        listAdapter = FuelTrackerListAdapter(MAINTENANCE, historyViewModel)
        subscribeObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRefresher()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewMaintenance.apply {
            adapter = listAdapter
            addItemDecoration(CustomItemDecoration(DECORATION_SPACING))
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            ItemTouchHelper(itemTouchHelper).attachToRecyclerView(this)
        }
    }

    private fun setupRefresher() {
        binding.swipeRefresherMaintenance.setOnRefreshListener {
            historyViewModel.syncAllTrips()
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
            val maintenance = listAdapter.currentList[position]
            showDeleteDialog(maintenance)
        }
    }

    private fun subscribeObservers() {
        historyViewModel.sortedMaintenanceHistory.observe(
            viewLifecycleOwner,
            Observer { sortedMaintenanceList ->

                listAdapter.apply {
                    submitList(null)
                    submitList(sortedMaintenanceList)
                }
            })
        historyViewModel.swipeLayout.observe(viewLifecycleOwner, Observer {
            binding.swipeRefresherMaintenance.isEnabled = !it
        })
    }

    private fun showDeleteDialog(maintenance: Maintenance) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.delete_alert_dialog_message))
            .setPositiveButton(
                "YES"
            ) { _, _ ->
                historyViewModel.deleteMaintenance(maintenance.id)
                Snackbar.make(
                    requireActivity().findViewById(R.id.rootLayout),
                    getString(R.string.maintenance_record_deleted),
                    Snackbar.LENGTH_LONG
                ).apply {
                    setActionTextColor(resources.getColor(R.color.secondaryDarkColor, null))
                    setAnchorView(R.id.bottomAppBar)
                    setAction(getString(R.string.undo)) {
                        undoDelete(maintenance)

                    }
                }.show()
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                listAdapter.notifyDataSetChanged()
                dialog.cancel()
            }
        dialogBuilder.show()

    }

    private fun undoDelete(maintenance: Maintenance) {
        historyViewModel.insertMaintenance(maintenance)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()

        inflater.inflate(R.menu.filter_maintenance_menu, menu)
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
            else -> super.onOptionsItemSelected(item)
        }
    }
}