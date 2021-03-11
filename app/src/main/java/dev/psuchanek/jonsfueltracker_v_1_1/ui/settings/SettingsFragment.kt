package dev.psuchanek.jonsfueltracker_v_1_1.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import dev.psuchanek.jonsfueltracker_v_1_1.R

class SettingsFragment: PreferenceFragmentCompat(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}