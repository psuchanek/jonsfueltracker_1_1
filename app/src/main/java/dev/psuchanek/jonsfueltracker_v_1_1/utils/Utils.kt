package dev.psuchanek.jonsfueltracker_v_1_1.utils


import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginTop
import dev.psuchanek.jonsfueltracker_v_1_1.databinding.ActivityMainBinding
import timber.log.Timber
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round


//Convert date string to timeInMillis in format yyyy-mm-dd hh:mm:ss
fun String.convertDateStringToTimestamp() = Timestamp.valueOf(this).time

fun Long.convertTimestampToDateString(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(this).toString()
}

//Convert date string to timeInMillis in format dd/MM/yyyy
fun String.convertToTimeInMillis(): Long {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.parse(this)!!.time
}

fun Long.formatDateForUI(): String {
    val dateFormat = SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault())
    return dateFormat.format(this).toString()
}

// Get vehicle id from type
fun getVehicleId(vehicleType: VehicleType) = when (vehicleType) {
    VehicleType.MICRA -> 1
    VehicleType.MIDGET -> 2
    VehicleType.SPRINTER -> 3
}

//Day and month help functions
fun getDay(dayOfMonth: Int) = when (dayOfMonth) {
    in 1..9 -> {
        "0$dayOfMonth"
    }
    else -> "$dayOfMonth"
}

fun getMonth(month: Int) = when (month) {
    in 1..9 -> {
        "0$month"
    }
    else -> "$month"
}

//Create date string
fun createDateString(year: Int, monthOfYear: Int, dayOfMonth: Int): String {
    return "${getDay(dayOfMonth)}/${getMonth(monthOfYear)}/$year"
}

//Capitalize words in string
@SuppressLint("DefaultLocale")
fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.toLowerCase().capitalize() }


//Date ranges
enum class TimePeriod {
    THREE_MONTHS, SIX_MONTHS, ONE_YEAR, THREE_YEARS
}

fun getTimePeriodTimestamp(timePeriod: TimePeriod): Long {
    var timeFormat: Int = 0
    var numberOf: Int = 0
    when (timePeriod) {
        TimePeriod.THREE_MONTHS -> {
            timeFormat = Calendar.MONTH
            numberOf = -3
        }
        TimePeriod.SIX_MONTHS -> {
            timeFormat = Calendar.MONTH
            numberOf = -6
        }
        TimePeriod.ONE_YEAR -> {
            timeFormat = Calendar.YEAR
            numberOf = -1
        }
        TimePeriod.THREE_YEARS -> {
            timeFormat = Calendar.YEAR
            numberOf = -3
        }
    }
    val dateReference = Date()
    val calendar = Calendar.getInstance()
    calendar.time = dateReference
    calendar.add(timeFormat, numberOf)
    return calendar.time.time
}


//Round-up
fun Float.round(decimals: Int): Float {
    var multiplier = 1.0f
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

//Convert litres to gallons
fun Float.convertToGallons() = this / LITRES_IN_GALLON

//Calculate pence per litre
fun calculatePencePerLitre(price: Float, litres: Float): Float {
    if (litres == 0f || price == 0f) return 0.0f
    return round((price / litres) * 1000) / 10
}

//Soft keyobard
fun hideKeyboardWhenDisplayed(context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}

//Layout margin change
fun changeMargin(bottomBarVisible: Boolean, binding: ActivityMainBinding) {
    val topMargin = binding.navHostFragment.marginTop
    Timber.d("DEBUG: this is top margin: $topMargin")

    val bottomMargin = when (bottomBarVisible) {
        true -> topMargin
        false -> 0
    }
    val params = CoordinatorLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    params.setMargins(0, topMargin, 0, bottomMargin)
    binding.navHostFragment.layoutParams = params


}

//Default vehicle list
val defaultVehicleList = listOf<String>(VEHICLE_ONE, VEHICLE_TWO, VEHICLE_THREE)






