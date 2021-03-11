package dev.psuchanek.jonsfueltracker_v_1_1

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener,
    Preference.SummaryProvider<ListPreference> {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
//        setSupportActionBar(binding.toolbar)

        val darkModeValues = resources.getStringArray(R.array.dark_mode_values)
        // The apps theme is decided depending upon the saved preferences on app startup
        when (PreferenceManager.getDefaultSharedPreferences(this)
            .getString(getString(R.string.dark_mode), getString(R.string.dark_mode_def_value))) {
            darkModeValues[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            darkModeValues[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            darkModeValues[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            darkModeValues[3] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
//        appBarConfiguration = AppBarConfiguration(navHostFragment.navController.graph)
//        setupActionBarWithNavController(navHostFragment.findNavController())
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val darkModeString = getString(R.string.dark_mode)
        key?.let {
            if (it == darkModeString) sharedPreferences?.let { pref ->
                val darkModeValues = resources.getStringArray(R.array.dark_mode_values)
                when (pref.getString(darkModeString, darkModeValues[0])) {
                    darkModeValues[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    darkModeValues[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    darkModeValues[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    darkModeValues[3] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun provideSummary(preference: ListPreference?): CharSequence =
        if (preference?.key == getString(R.string.dark_mode)) preference.entry
        else "Unknown Preference"


}