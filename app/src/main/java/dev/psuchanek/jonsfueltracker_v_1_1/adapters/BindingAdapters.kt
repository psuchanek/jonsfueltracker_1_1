package dev.psuchanek.jonsfueltracker_v_1_1.adapters

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.google.android.material.textview.MaterialTextView
import dev.psuchanek.jonsfueltracker_v_1_1.models.Vehicle
import dev.psuchanek.jonsfueltracker_v_1_1.utils.UNKNOWN_VEHICLE
import dev.psuchanek.jonsfueltracker_v_1_1.utils.defaultVehicleList

@BindingAdapter("vehicleID", "vehicleList")
fun setVehicleName(textView: MaterialTextView, vehicleID: Int, vehicleList: LiveData<List<Vehicle>>?) {
    if(vehicleList?.value?.isNullOrEmpty()!!) {
        try {
            textView.text = defaultVehicleList[vehicleID - 1]
        }catch (e: ArrayIndexOutOfBoundsException) {
            textView.text = UNKNOWN_VEHICLE
        }
        return
    }
    vehicleList.value?.let {
        textView.text = vehicleList.value!![vehicleID -1].vehicleName
        return
    }



}