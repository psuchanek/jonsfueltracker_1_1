package dev.psuchanek.jonsfueltracker_v_1_1.ui.dash

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.psuchanek.jonsfueltracker_v_1_1.BaseFragment
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.FragmentDashboardBinding
import dev.psuchanek.jonsfueltracker_v_1_1.ui.MainViewModel
import dev.psuchanek.jonsfueltracker_v_1_1.utils.RIGHT_AXIS_LABEL_COUNT
import dev.psuchanek.jonsfueltracker_v_1_1.utils.TimePeriod
import dev.psuchanek.jonsfueltracker_v_1_1.utils.getTimePeriodTimestamp

@AndroidEntryPoint
class DashboardFragment : BaseFragment(R.layout.fragment_dashboard) {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        subscribeObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLineChart()
        binding.apply {
            lineChartButtonLayout.apply {
                btnThreeMonths.setOnClickListener(onClick())
                btnSixMonths.setOnClickListener(onClick())
                btnOneYear.setOnClickListener(onClick())
                btnThreeYears.setOnClickListener(onClick())
                btnAll.setOnClickListener(onClick())
            }
            lineChart.onChartGestureListener = lineChartListener()
            tabLayout.addOnTabSelectedListener(tabLayoutClickListener())
        }
    }


    private fun setupLineChart() {
        binding.lineChart.apply {
            setDrawBorders(false)
            isClickable = true
            legend.isEnabled = false
            description.text = ""
            setNoDataText(context.getString(R.string.no_data_for_this_period))
            xAxis.apply {
                setDrawGridLines(false)
                setDrawLabels(false)
                position = XAxis.XAxisPosition.BOTTOM
                axisLineColor = Color.WHITE
                textColor = Color.WHITE
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
                textColor = resources.getColor(R.color.white, null)
                setDrawGridLines(false)
            }
        }
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
                val listOfEntries = tripList.indices.map { i ->
                    Entry(i.toFloat(), tripList[i].fuelCost)
                }
                val lineDataSet = getLineDataSet(listOfEntries)
                binding.lineChart.apply {
                    animateX(1000)
                    data = LineData(lineDataSet)
                    this.invalidate()
                }

            }

        })

        viewModel.mostRecentTrip?.observe(viewLifecycleOwner, Observer { mostRecentTrip ->
            binding.lastTripLayout.apply {
                trip = mostRecentTrip
            }
        })
    }

    private fun getLineDataSet(entries: List<Entry>): LineDataSet {
        return LineDataSet(entries, "").apply {
            lineWidth = 4f
            color = resources.getColor(R.color.colorAccent, null)
            setDrawFilled(true)
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        }.also {
            if (entries.size > 50) it.setDrawValues(false)
        }
    }

    private fun onClick() = View.OnClickListener { view ->
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

            //TODO: style color according to APP Theme
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


    private fun lineChartListener() = object : OnChartGestureListener {
        override fun onChartGestureEnd(
            me: MotionEvent?,
            lastPerformedGesture: ChartTouchListener.ChartGesture?
        ) {
            when (lastPerformedGesture?.ordinal == ACTION_UP) {
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
            binding.lineChart.highlightValues(emptyArray())
        }

        override fun onChartDoubleTapped(me: MotionEvent?) {
            binding.lineChart.highlightValues(emptyArray())
        }

        override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
        }

    }

}