package dev.psuchanek.jonsfueltracker_v_1_1.ui.add

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
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
import dev.psuchanek.jonsfueltracker_v_1_1.utils.calculatePencePerLitre
import dev.psuchanek.jonsfueltracker_v_1_1.utils.createDateString
import dev.psuchanek.jonsfueltracker_v_1_1.utils.defaultVehicleList
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class AddTripFragment : BaseFragment(R.layout.fragment_add_trip) {

    private val addViewModel: AddViewModel by viewModels()

    private lateinit var binding: FragmentAddTripBinding

    private var vehicleId = 0
    private var lastKnownMileage = 0L
    private var price: String = ""
    private var litres: String = ""
    private var timestamp: Long = 0L

    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private var spinnerVehicleList: List<String> = defaultVehicleList

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_trip, container, false)
        subscribeObservers()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSpinner()
        initDate()
        binding.apply {
            evDate.setOnClickListener { launchDatePickerDialog() }
            evTripMileage.addTextChangedListener(textWatcher(evTripMileage.id))
            evPrice.addTextChangedListener(textWatcher(evPrice.id))
            evLitres.addTextChangedListener(textWatcher(evLitres.id))
            dropDownVehicle.onItemClickListener = spinnerOnItemClickListener()
        }
    }

    private fun spinnerOnItemClickListener() =
        AdapterView.OnItemClickListener { _, _, position, _ ->
            vehicleId = position + 1
            addViewModel.getCurrentMileage(vehicleId)
        }

    private fun insertTrip() {
        Timber.d("TIMESTAMP: now:${System.currentTimeMillis()}, chosen: $timestamp")
        val stationName = binding.evPetrolStation.text.toString()
        val price = binding.evPrice.text.toString()
        val fuelVolume = binding.evLitres.text.toString()
        addViewModel.insertTrip(
            stationName = stationName,
            timestamp = timestamp,
            vehicleId = vehicleId,
            price = price,
            ppl = "123",
            fuelVolume = fuelVolume,
            tripMileage = "123",
            totalMileage = "123"
        )
    }

    private fun textWatcher(viewId: Int) = object : TextWatcher {
        override fun afterTextChanged(string: Editable?) {
            when (viewId) {
                binding.evPrice.id -> {
                    price = string.toString()
                }
                binding.evLitres.id -> {
                    litres = string.toString()
                }
            }

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (viewId) {
                binding.evTripMileage.id -> {
                    if (!s.isNullOrEmpty()) {
                        val totalMileageString =
                            (s.toString().toInt() + lastKnownMileage).toString()
                        binding.evTotalMileage.setText(totalMileageString)
                    }
                }
                binding.evLitres.id -> {
                    s?.let {
                        litres = it.toString()
                    }
                    if (s.toString().isEmpty() || price.isEmpty()) {
                        binding.evPencePerLitre.setText("0.0")
                    }
                    if (!s.isNullOrEmpty() && !price.isNullOrEmpty()) {
                        val pplString =
                            calculatePencePerLitre(price.toFloat(), litres.toFloat()).toString()
                        binding.evPencePerLitre.setText(pplString)
                    }

                }
                binding.evPrice.id -> {
                    s?.let {
                        price = it.toString()
                    }
                    if (s.isNullOrEmpty() || litres.isNullOrEmpty()) {
                        binding.evPencePerLitre.setText(0.0.toString())
                    }

                    if (!s.isNullOrEmpty() && !litres.isNullOrEmpty()) {
                        val pplString =
                            calculatePencePerLitre(price.toFloat(), litres.toFloat()).toString()
                        binding.evPencePerLitre.setText(pplString)
                    }
                }
            }

        }

    }


    private fun initDate() {
        val dateTimestamp = Calendar.getInstance().timeInMillis
        timestamp = dateTimestamp
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
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                timestamp = calendar.timeInMillis

                setDateString(year, monthOfYear, dayOfMonth)
            },
            yearFromCalendar,
            monthFromCalendar,
            dayFromCalendar
        )
        datePickerDialog.show()
    }

    private fun setDateString(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val dateString = createDateString(year, monthOfYear, dayOfMonth)
        binding.evDate.setText(dateString)
    }


    private fun initSpinner() {
        spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            spinnerVehicleList
        ).also { adapter ->
            binding.dropDownVehicle.setAdapter(adapter)
        }
    }

    private fun subscribeObservers() {
        addViewModel.lastKnownMileage.observe(viewLifecycleOwner, Observer {
            lastKnownMileage = it
            if (it == null) {
                binding.evTotalMileage.setText(0)
            }
            binding.evTotalMileage.setText(it.toString())

        })

        addViewModel.submitStatus.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                Status.ERROR -> {
                    showSnackbar(getString(R.string.fileds_missing))
                }
                Status.SUCCESS -> {
                    findNavController().navigate(R.id.action_addFragment_to_dashboardFragment)
                    showSnackbar(getString(R.string.trip_added_successfully))
                }
                Status.LOADING -> {
                    /* NO-OP */
                }

            }

        })

        addViewModel.observeAllVehicles.observe(viewLifecycleOwner, Observer { vehicleList ->
            vehicleList.isNotEmpty().let {
                spinnerVehicleList = List(vehicleList.size, init = {
                    vehicleList[it].vehicleName
                })
                initSpinner()
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btnSubmit -> {
                insertTrip()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}