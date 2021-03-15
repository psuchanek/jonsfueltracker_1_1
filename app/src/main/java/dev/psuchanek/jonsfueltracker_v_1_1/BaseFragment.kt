package dev.psuchanek.jonsfueltracker_v_1_1

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment(layoutID: Int) : Fragment(layoutID) {
    //Snackbar utility
    fun showSnackbar(message: String) {
        Snackbar.make(
            requireActivity().findViewById(R.id.rootLayout),
            message,
            Snackbar.LENGTH_SHORT
        )
            .setAnchorView(R.id.fabAddTrip)
            .show()
    }
}