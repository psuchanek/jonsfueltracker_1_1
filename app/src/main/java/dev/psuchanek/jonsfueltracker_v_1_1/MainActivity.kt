package dev.psuchanek.jonsfueltracker_v_1_1

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.ActivityMainBinding
import dev.psuchanek.jonsfueltracker_v_1_1.utils.changeMargin
import dev.psuchanek.jonsfueltracker_v_1_1.utils.hideKeyboardWhenDisplayed

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener,
    Preference.SummaryProvider<ListPreference> {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var appBarConfig: AppBarConfiguration
    private var softKeyboardVisible: Boolean = false
    val isKeyboardVisible: Boolean
        get() = softKeyboardVisible


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)


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

        setSupportActionBar(binding.toolbar)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        appBarConfig = AppBarConfiguration(setOf(R.id.dashboardFragment, R.id.historyFragment))
        setupActionBarWithNavController(navHostFragment.findNavController(), appBarConfig)

        binding.bottomNavigationView.apply {
            background = null
            setupWithNavController(navHostFragment.findNavController())
        }

        navHostFragment.findNavController()
            .addOnDestinationChangedListener(destinationChangeListener())

        binding.fabAddTrip.setOnClickListener(fabAddTripClickListener())
        binding.rootLayout.viewTreeObserver.addOnGlobalLayoutListener(softKeyboardListener())

        binding.root
    }

    private fun softKeyboardListener() = ViewTreeObserver.OnGlobalLayoutListener {
        val rect = Rect()
        binding.rootLayout.getWindowVisibleDisplayFrame(rect)
        val screenHeight = binding.rootLayout.rootView.height
        val keypadHeight = screenHeight - rect.bottom
        softKeyboardVisible = keypadHeight > screenHeight * 0.15
    }

    private fun fabAddTripClickListener() = View.OnClickListener {
        navHostFragment.findNavController()
            .navigate(R.id.action_to_addTripFragment)
    }


    private fun destinationChangeListener() =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dashboardFragment, R.id.historyFragment -> {
                    if(isKeyboardVisible) hideKeyboardWhenDisplayed(this)
                    with(binding) {
                        changeMargin(true, binding)
                        toolbar.title = resources.getString(R.string.app_name)
                        tvVersion.visibility = View.GONE
                        bottomAppBar.visibility = View.VISIBLE
                        fabAddTrip.show()
                    }

                }
                R.id.settingsFragment -> {
                    with(binding) {
                        bottomAppBar.visibility = View.GONE
                        changeMargin(false, binding)
                        fabAddTrip.hide()
                        tvVersion.visibility = View.VISIBLE
                    }
                }
                else -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    with(binding) {
                        changeMargin(false, binding)
                        bottomAppBar.visibility = View.GONE
                        tvVersion.visibility = View.GONE
                        fabAddTrip.hide()
                    }

                }


            }
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                navHostFragment.findNavController().navigate(R.id.action_to_settingsFragment)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
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

    override fun onResume() {
        super.onResume()
        if(isKeyboardVisible) {
            hideKeyboardWhenDisplayed(this)
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

    override fun onBackPressed() {
        super.onBackPressed()
        if(isKeyboardVisible) {
            hideKeyboardWhenDisplayed(this)
        }
    }

}