package dev.psuchanek.jonsfueltracker_v_1_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setupWithNavController(navHostFragment.findNavController())
        binding.bottomNavigationView.apply {
            background = null
            setupWithNavController(navHostFragment.findNavController())
        }
        navHostFragment.findNavController()
            .addOnDestinationChangedListener(destinationChangeListener())
        binding.fabAddTrip.setOnClickListener(fabAddTripClickListener())

        binding.root
    }

    private fun fabAddTripClickListener() = View.OnClickListener {
        navHostFragment.findNavController()
            .navigate(R.id.action_to_addTripFragment)
    }

    private fun destinationChangeListener() =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dashboardFragment, R.id.historyFragment -> {
                    binding.bottomAppBar.visibility = View.VISIBLE
                    binding.fabAddTrip.show()
                }
                else -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    binding.bottomAppBar.visibility = View.GONE
                    binding.fabAddTrip.hide()
                }


            }
        }

    override fun onBackPressed() {
        super.onBackPressed()

    }
}