package dev.psuchanek.jonsfueltracker_v_1_1.ui.add

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.psuchanek.jonsfueltracker_v_1_1.BaseFragment
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.FragmentAddMaintenanceBinding
import dev.psuchanek.jonsfueltracker_v_1_1.utils.Status
import dev.psuchanek.jonsfueltracker_v_1_1.utils.createDateString
import dev.psuchanek.jonsfueltracker_v_1_1.utils.defaultVehicleList
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class AddMaintenanceFragment : BaseFragment(R.layout.fragment_add_maintenance) {

    private val addViewModel: AddViewModel by viewModels()

    private lateinit var binding: FragmentAddMaintenanceBinding
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private var spinnerVehicleList: List<String> = defaultVehicleList

    private var timestamp: Long = 0L
    private var lastKnownMileage: Int = 0
    private var vehicleId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentAddMaintenanceBinding.inflate(inflater, container, false)
        subscribeObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDate()
        initSpinner()
        with(binding) {
            evDate.setOnClickListener { launchDatePickerDialog() }
            dropDownVehicle.onItemClickListener = spinnerOnItemClickListener()
        }
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
            lastKnownMileage = it.toInt()
            if (it == null) {
                binding.evCurrentMileage.setText(0)
                return@Observer
            }
            binding.evCurrentMileage.setText(it.toString())

        })

        addViewModel.submitStatus.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                Status.ERROR -> {
                    showSnackbar(getString(R.string.fileds_missing))
                }
                Status.SUCCESS -> {
                    findNavController().navigate(R.id.action_addFragment_to_dashboardFragment)
                    showSnackbar(getString(R.string.maintenance_added_successfully))
                }
                Status.LOADING -> {
                    /* NO-OP */
                }

            }

        })

        addViewModel.observeAllVehicles.observe(viewLifecycleOwner, Observer { vehicleList ->
            if (vehicleList.isNotEmpty()) {
                spinnerVehicleList = List(vehicleList.size, init = {
                    vehicleList[it].vehicleName
                })
                initSpinner()
                Timber.d("DEBUG: vehicleList: $vehicleList")
            }
        })
    }

    private fun spinnerOnItemClickListener() =
        AdapterView.OnItemClickListener { _, _, position, _ ->
            vehicleId = position + 1
            addViewModel.getCurrentMileage(vehicleId)
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

    private fun insertMaintenance() {
        val workShopName = binding.evWorkshopName.text.toString()
        val workDoneDescription = binding.evWorkDoneDescription.text.toString()
        val workPrice = binding.evWorkPrice.text.toString()
        val mileage = binding.evCurrentMileage.toString().toInt()

        addViewModel.insertMaintenance(
            timestamp,
            vehicleId,
            workShopName,
            workDoneDescription,
            workPrice,
            mileage
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btnSubmit -> {
                insertMaintenance()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}