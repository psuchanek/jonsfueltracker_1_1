package dev.psuchanek.jonsfueltracker_v_1_1.ui.add

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.FragmentAddTripBinding
import dev.psuchanek.jonsfueltracker_v_1_1.other.Status
import dev.psuchanek.jonsfueltracker_v_1_1.other.showSnackbar
import java.util.*

@AndroidEntryPoint
class AddTripFragment : Fragment() {

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
            btnSubmit.setOnClickListener { submitTrip() }
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

    private fun submitTrip() {
        val date = binding.evDate.text.toString()
        val stationName = binding.evPetrolStation.text.toString()
        val price = binding.evPrice.text.toString()
        val fuelVolume = binding.evLitres.text.toString()
        addTripViewModel.submitTrip(
            date = date,
            stationName = stationName,
            vehicleId = vehicleId,
            price = price,
            ppl = "",
            fuelVolume = fuelVolume,
            tripMileage = "",
            totalMileage = ""
        )
    }

    private fun initDate() {
        val dateTimestamp = Calendar.getInstance().timeInMillis
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.evDate.setText(dateFormat.format(dateTimestamp))
    }

    private fun launchDatePickerDialog() {

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
                    binding.addTripLayout.showSnackbar(getString(R.string.fileds_missing))
                }
                Status.SUCCESS -> {
                    findNavController().navigate(R.id.action_addTripFragment_to_dashboardFragment)
                }
                Status.EXCEPTION -> {
                    binding.addTripLayout.showSnackbar(getString(R.string.something_went_wrong))
                }
            }

        })
    }

}