package dev.psuchanek.jonsfueltracker_v_1_1.other

import androidx.core.text.trimmedLength
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

fun String.convertDateStringToTimestamp() = Timestamp.valueOf(this).time

fun Long.convertTimestampToDateString(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(this).toString()
}


//Date ranges
enum class TimePeriod {
    THREE_MONTHS, SIX_MONTHS, ONE_YEAR, THREE_YEARS
}
fun getTimePeriodTimestamp(timePeriod: TimePeriod): Long {
    var timeFormat: Any
    var numberOf: Int = 0
    when(timePeriod) {
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
    val dateRefrence = Date()
    val calendar = Calendar.getInstance()
    calendar.time = dateRefrence
    calendar.add(timeFormat, numberOf)
    return calendar.time.time
}

