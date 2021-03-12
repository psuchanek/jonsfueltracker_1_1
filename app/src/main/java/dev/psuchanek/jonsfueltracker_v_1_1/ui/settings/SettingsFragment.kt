package dev.psuchanek.jonsfueltracker_v_1_1.ui.settings

import android.os.Bundle
import android.view.*
import androidx.preference.PreferenceFragmentCompat
import dev.psuchanek.jonsfueltracker_v_1_1.R

class SettingsFragment: PreferenceFragmentCompat(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }
}