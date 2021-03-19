package dev.psuchanek.jonsfueltracker_v_1_1.ui.add

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.psuchanek.jonsfueltracker_v_1_1.BaseFragment
import dev.psuchanek.jonsfueltracker_v_1_1.R
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.FragmentAddVehicleBinding
import dev.psuchanek.jonsfueltracker_v_1_1.utils.Status

@AndroidEntryPoint
class AddVehicleFragment : BaseFragment(R.layout.fragment_add_vehicle) {

    private val addViewModel: AddViewModel by viewModels()

    private lateinit var binding: FragmentAddVehicleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentAddVehicleBinding.inflate(inflater, container, false)
        subscribeObservers()
        return binding.root
    }

    private fun subscribeObservers() {
        addViewModel.submitStatus.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                Status.ERROR -> {
                    showSnackbar(getString(R.string.fileds_missing))
                }
                Status.SUCCESS -> {
                    findNavController().navigate(R.id.action_addFragment_to_dashboardFragment)
                    showSnackbar(getString(R.string.vehicle_added_successfully))
                }
                Status.LOADING -> {
                    /* NO-OP */
                }

            }

        })
    }

    private fun insertVehicle() {
        addViewModel.insertVehicle(binding.evNewVehicleName.text.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btnSubmit -> {
                insertVehicle()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}