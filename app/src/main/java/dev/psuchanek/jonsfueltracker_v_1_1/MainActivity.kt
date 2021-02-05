package dev.psuchanek.jonsfueltracker_v_1_1

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

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
                    with(binding) {
                        bottomAppBar.visibility = View.VISIBLE
                        fabAddTrip.show()
                    }

                }
                else -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    with(binding) {
                        bottomAppBar.visibility = View.GONE
                        fabAddTrip.hide()
                    }

                }


            }
        }

    override fun onBackPressed() {
        super.onBackPressed()

    }
}