package dev.psuchanek.jonsfueltracker_v_1_1.ui.dash

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.psuchanek.jonsfueltracker_v_1_1.BaseFragment
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.adapters.FuelTrackerListAdapter
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.FragmentDashboardBinding
import dev.psuchanek.jonsfueltracker_v_1_1.models.FuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.models.Vehicle
import dev.psuchanek.jonsfueltracker_v_1_1.models.asFuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.ui.MainViewModel
import dev.psuchanek.jonsfueltracker_v_1_1.utils.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class DashboardFragment : BaseFragment(R.layout.fragment_dashboard) {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPreferences
    private lateinit var binding: FragmentDashboardBinding
    private var mostRecentTrip: FuelTrackerTrip? = null
    private lateinit var vehicleAdapter: FuelTrackerListAdapter<Vehicle>
    private val lineDataSets = arrayListOf<LineDataSet>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        if (sharedPrefs.getBoolean(FIRST_LAUNCH, true)) {
            fetchInitialData()
            sharedPrefs.edit().clear().apply()
            sharedPrefs.edit().putBoolean(FIRST_LAUNCH, false).apply()
        }
        subscribeObservers()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view, savedInstanceState
        )
        setupRecyclerViews()
        setupSwipeRefresher()
        setupLineChart()
        binding.apply {
            lineChartButtonLayout.apply {
                btnThreeMonths.setOnClickListener(onChartButtonClick())
                btnSixMonths.setOnClickListener(onChartButtonClick())
                btnOneYear.setOnClickListener(onChartButtonClick())
                btnThreeYears.setOnClickListener(onChartButtonClick())
                btnAll.setOnClickListener(onChartButtonClick())
            }
            lineChart.apply {
                setOnChartValueSelectedListener(lineChartSelectListener())
                onChartGestureListener = lineChartGestureListener()
            }
            tabLayout.addOnTabSelectedListener(tabLayoutClickListener())
        }
    }

    private fun setupSwipeRefresher() {
        binding.swipeRefresherDash.isRefreshing = false
        binding.swipeRefresherDash.setOnRefreshListener {
            if (!sharedPrefs.getBoolean(FIRST_LAUNCH, true)) {
                binding.swipeRefresherDash.isRefreshing = false
                binding.lastTripLayout.trip = mostRecentTrip
            } else {
                fetchInitialData()
                viewModel.fetchData()
            }

        }
    }

    private fun setupRecyclerViews() {
        vehicleAdapter = FuelTrackerListAdapter<Vehicle>(VEHICLE)
        binding.vehicleOverviewLayout.vehicleRecyclerView.apply {
            adapter = vehicleAdapter
            addItemDecoration(CustomItemDecoration(DECORATION_SPACING))
        }
    }


    private fun setupLineChart() {
        binding.lineChart.apply {
            setDrawBorders(false)
            setScaleEnabled(false)
            isClickable = true
            legend.isEnabled = false
            description.text = ""
            setNoDataTextColor(resources.getColor(R.color.secondaryDarkColor, null))
            setNoDataText(context.getString(R.string.no_data_for_this_period))
            xAxis.apply {
                setDrawGridLines(false)
                setDrawLabels(false)
                position = XAxis.XAxisPosition.BOTTOM
                axisLineColor = Color.BLACK
            }
            axisLeft.apply {
                setDrawAxisLine(false)
                setDrawLabels(false)
                setDrawGridLines(false)
            }
            axisRight.apply {
                setDrawAxisLine(false)
                setDrawLabels(true)
                labelCount = RIGHT_AXIS_LABEL_COUNT
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    textColor = resources.getColor(R.color.primaryTextColor, null)
                }
                setDrawGridLines(false)
            }
        }
    }

    private fun fetchInitialData() {
        viewModel.fetchInitialData.observe(viewLifecycleOwner, Observer { event ->
            event?.let {
                val result = it.peekContent()
                when (result.status) {
                    Status.LOADING -> {
                        binding.swipeRefresherDash.isRefreshing = true
                    }
                    Status.SUCCESS -> {
                        binding.swipeRefresherDash.isRefreshing = true
                        val mostRecentTrip = it.getContentIfNotHandled()?.let { resource ->
                            resource.data?.maxBy { trip ->
                                trip.timestamp
                            }

                        }
                        this.mostRecentTrip = mostRecentTrip?.asFuelTrackerTrip()
                        binding.lastTripLayout.apply {
                            trip = mostRecentTrip?.asFuelTrackerTrip()
                            binding.swipeRefresherDash.isRefreshing = false
                        }
                        viewModel.fetchInitialData.removeObservers(viewLifecycleOwner)
                    }
                    Status.ERROR -> {
                        binding.swipeRefresherDash.isRefreshing = true
                        it.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { errorMessage ->
                                showSnackbar(errorMessage)
                            }
                        }
                    }
                }

            }
        })
    }

    private fun subscribeObservers() {
        viewModel.tripsByTimestampRange.observe(viewLifecycleOwner, Observer { event ->
            event?.getContentIfNotHandled()?.let { tripList ->
                if (tripList.isEmpty()) {
                    binding.lineChart.apply {
                        data = null
                        invalidate()
                    }
                    return@let
                }
                populateLineChart(tripList)

            }

        })


        viewModel.mostRecentTrip?.observe(viewLifecycleOwner, Observer { mostRecentTrip ->
            if (mostRecentTrip == null) {
                return@Observer
            }
            this.mostRecentTrip = mostRecentTrip.asFuelTrackerTrip()
            binding.lastTripLayout.apply {
                trip = mostRecentTrip.asFuelTrackerTrip()
            }


        })

        viewModel.vehicleList.observe(viewLifecycleOwner, Observer { vehicleList ->
            vehicleList?.let {
                Timber.d("DEBUG: vehicleList: $it")
                vehicleAdapter.submitList(vehicleList)
            }

        })
    }

    private fun populateLineChart(tripList: List<FuelTrackerTrip>) {
        val listOfEntries = tripList.indices.map { i ->
            Entry(i.toFloat(), tripList[i].fuelCost, tripList[i])
        }
        val lineDataSet = getLineDataSet(listOfEntries)
        binding.lineChart.apply {
            animateX(500)
            data = LineData(lineDataSet)
            this.invalidate()
        }
    }


    private fun getLineDataSet(entries: List<Entry>): LineDataSet {
        return LineDataSet(entries, "").apply {
            lineWidth = 4f
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                valueTextColor = resources.getColor(R.color.primaryTextColor, null)
            }
            color = resources.getColor(R.color.secondaryDarkColor, null)
            setDrawHorizontalHighlightIndicator(false)
            setDrawFilled(true)
            setCircleColor(resources.getColor(R.color.secondaryLightColor, null))
            fillColor = resources.getColor(R.color.secondaryLightColor, null)
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        }.also {
            if (entries.size > 50) it.setDrawValues(false)
        }
    }


    private fun onChartButtonClick() = View.OnClickListener { view ->
        when (view.id) {
            R.id.btnThreeMonths -> {
                changeButton(view.id)
                viewModel.getTripsByTimestampRange(getTimePeriodTimestamp(TimePeriod.THREE_MONTHS))
            }
            R.id.btnSixMonths -> {
                changeButton(view.id)
                viewModel.getTripsByTimestampRange(getTimePeriodTimestamp(TimePeriod.SIX_MONTHS))
            }
            R.id.btnOneYear -> {
                changeButton(view.id)
                viewModel.getTripsByTimestampRange(getTimePeriodTimestamp(TimePeriod.ONE_YEAR))
            }
            R.id.btnThreeYears -> {
                changeButton(view.id)
                viewModel.getTripsByTimestampRange(getTimePeriodTimestamp(TimePeriod.THREE_YEARS))
            }
            R.id.btnAll -> {
                changeButton(view.id)
                viewModel.getTripsByTimestampRange(0L)
            }
        }

    }

    private fun changeButton(viewID: Int) {

        val colorArray = ArrayList<Int>().apply {
            add(resources.getColor(R.color.white, null))
            add(resources.getColor(R.color.grey, null))
        }
        val backgroundArray = ArrayList<Drawable>().apply {
            add(resources.getDrawable(R.drawable.btn_line_chart_shape, null))
            add(resources.getDrawable(R.drawable.btn_line_chart_shape_transparent, null))
        }

        when (viewID) {
            R.id.btnThreeMonths -> {
                swapColors(
                    colorArray[0],
                    colorArray[1],
                    colorArray[1],
                    colorArray[1],
                    colorArray[1]
                )
                swapBackgrounds(
                    backgroundArray[0],
                    backgroundArray[1],
                    backgroundArray[1],
                    backgroundArray[1],
                    backgroundArray[1]
                )
            }
            R.id.btnSixMonths -> {
                swapColors(
                    colorArray[1],
                    colorArray[0],
                    colorArray[1],
                    colorArray[1],
                    colorArray[1]
                )
                swapBackgrounds(
                    backgroundArray[1],
                    backgroundArray[0],
                    backgroundArray[1],
                    backgroundArray[1],
                    backgroundArray[1]
                )
            }
            R.id.btnOneYear -> {
                swapColors(
                    colorArray[1],
                    colorArray[1],
                    colorArray[0],
                    colorArray[1],
                    colorArray[1]
                )
                swapBackgrounds(
                    backgroundArray[1],
                    backgroundArray[1],
                    backgroundArray[0],
                    backgroundArray[1],
                    backgroundArray[1]
                )
            }
            R.id.btnThreeYears -> {
                swapColors(
                    colorArray[1],
                    colorArray[1],
                    colorArray[1],
                    colorArray[0],
                    colorArray[1]
                )
                swapBackgrounds(
                    backgroundArray[1],
                    backgroundArray[1],
                    backgroundArray[1],
                    backgroundArray[0],
                    backgroundArray[1]
                )
            }
            R.id.btnAll -> {
                swapColors(
                    colorArray[1],
                    colorArray[1],
                    colorArray[1],
                    colorArray[1],
                    colorArray[0]
                )
                swapBackgrounds(
                    backgroundArray[1],
                    backgroundArray[1],
                    backgroundArray[1],
                    backgroundArray[1],
                    backgroundArray[0]
                )
            }
        }
    }

    private fun swapBackgrounds(vararg background: Drawable) {
        binding.lineChartButtonLayout.apply {
            btnThreeMonths.background = background[0]
            btnSixMonths.background = background[1]
            btnOneYear.background = background[2]
            btnThreeYears.background = background[3]
            btnAll.background = background[4]
        }
    }

    private fun swapColors(
        vararg colors: Int
    ) {
        binding.lineChartButtonLayout.apply {
            btnThreeMonths.setTextColor(colors[0])
            btnSixMonths.setTextColor(colors[1])
            btnOneYear.setTextColor(colors[2])
            btnThreeYears.setTextColor(colors[3])
            btnAll.setTextColor(colors[4])
        }
    }

    private fun tabLayoutClickListener() = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            when (tab?.position) {
                0 -> {
                    binding.maintenanceOverviewLayout.maintenanceLayout.visibility = View.INVISIBLE
                    binding.vehicleOverviewLayout.vehicleLayout.visibility = View.VISIBLE
                }
                1 -> {
                    binding.maintenanceOverviewLayout.maintenanceLayout.visibility = View.VISIBLE
                    binding.vehicleOverviewLayout.vehicleLayout.visibility = View.INVISIBLE
                }
            }
        }

    }

    private fun lineChartSelectListener() = object : OnChartValueSelectedListener {
        override fun onNothingSelected() {
        }

        override fun onValueSelected(e: Entry?, h: Highlight?) {
            e?.let {
                binding.lastTripLayout.lastTripLabel.text = "SELECTED TRIP:"
                binding.lastTripLayout.trip = it.data as FuelTrackerTrip
                binding.swipeRefresherDash.isEnabled = false
            }
        }
    }

    private fun lineChartGestureListener() = object : OnChartGestureListener {
        override fun onChartGestureEnd(
            me: MotionEvent?,
            lastPerformedGesture: ChartTouchListener.ChartGesture?
        ) {
            if (me?.action == MotionEvent.ACTION_UP) {
                binding.lineChart.highlightValues(null)
                binding.lastTripLayout.apply {
                    trip = mostRecentTrip
                    lastTripLabel.text = "LAST TRIP:"
                }
                binding.swipeRefresherDash.isEnabled = true
            }
        }

        override fun onChartFling(
            me1: MotionEvent?,
            me2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ) {

        }

        override fun onChartSingleTapped(me: MotionEvent?) {
        }

        override fun onChartGestureStart(
            me: MotionEvent?,
            lastPerformedGesture: ChartTouchListener.ChartGesture?
        ) {
        }

        override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
        }

        override fun onChartLongPressed(me: MotionEvent?) {
        }

        override fun onChartDoubleTapped(me: MotionEvent?) {
        }

        override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
        }

    }


}
