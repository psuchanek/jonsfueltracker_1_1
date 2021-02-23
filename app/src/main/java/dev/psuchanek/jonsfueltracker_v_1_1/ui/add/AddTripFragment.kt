package dev.psuchanek.jonsfueltracker_v_1_1.ui.add

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.psuchanek.jonsfueltracker_v_1_1.BaseFragment
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.FragmentAddTripBinding
import dev.psuchanek.jonsfueltracker_v_1_1.utils.Status
import dev.psuchanek.jonsfueltracker_v_1_1.utils.getDay
import dev.psuchanek.jonsfueltracker_v_1_1.utils.getMonth
import java.util.*

@AndroidEntryPoint
class AddTripFragment : BaseFragment(R.layout.fragment_add_trip) {

    private val addTripViewModel: AddTripViewModel by viewModels()

    private lateinit var binding: FragmentAddTripBinding

    private var vehicleId = 0
    private var lastKnownMileage = 0L
    private var priceInFloat = 0.0f
    private var litres = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_trip, container, false)
        binding.apply {
            evDate.setOnClickListener { launchDatePickerDialog() }
            btnSubmit.setOnClickListener { insertTrip() }
        }
        subscribeObservers()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSpinner()
        initDate()
        binding.dropDownVehicle.onItemClickListener = spinnerOnItemClickListener()
    }

    private fun spinnerOnItemClickListener() =
        AdapterView.OnItemClickListener { _, _, position, _ ->
            vehicleId = position + 1
            addTripViewModel.getCurrentMileage(vehicleId)
        }

    private fun insertTrip() {
        val date = binding.evDate.text.toString()
        val stationName = binding.evPetrolStation.text.toString()
        val price = binding.evPrice.text.toString()
        val fuelVolume = binding.evLitres.text.toString()
        addTripViewModel.insertTrip(
            date = date,
            stationName = stationName,
            vehicleId = vehicleId,
            price = price,
            ppl = "123",
            fuelVolume = fuelVolume,
            tripMileage = "123",
            totalMileage = "123"
        )
    }

    private fun initDate() {
        val dateTimestamp = Calendar.getInstance().timeInMillis
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.evDate.setText(dateFormat.format(dateTimestamp))
    }

    private fun launchDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val yearFromCalendar = calendar.get(Calendar.YEAR)
        val monthFromCalendar = calendar.get(Calendar.MONTH)
        val dayFromCalendar = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                setDateString(year, monthOfYear, dayOfMonth)
            },
            yearFromCalendar,
            monthFromCalendar,
            dayFromCalendar
        )
        datePickerDialog.show()
    }

    private fun setDateString(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val dateString = "${getDay(dayOfMonth)}/${getMonth(monthOfYear)}/$year"
        binding.evDate.setText(dateString)
    }


    private fun initSpinner() {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.vehicle_names)
        ).also { adapter ->
            binding.dropDownVehicle.setAdapter(adapter)
        }
    }

    private fun subscribeObservers() {
        addTripViewModel.lastKnownMileage.observe(viewLifecycleOwner, Observer {
            lastKnownMileage = it
            if (it != 0L) {
                binding.evTotalMileage.setText(it.toString())
            }
        })

        addTripViewModel.submitTripStatus.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                Status.ERROR -> {
                    showSnackbar(getString(R.string.fileds_missing))
                }
                Status.SUCCESS -> {
                    findNavController().navigate(R.id.action_addTripFragment_to_dashboardFragment)
                    showSnackbar(getString(R.string.trip_added_successfully))
                }
                Status.LOADING -> {
                    /* NO-OP */
                }

            }

        })
    }

}