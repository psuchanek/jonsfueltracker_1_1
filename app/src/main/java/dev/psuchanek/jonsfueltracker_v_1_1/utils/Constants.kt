package dev.psuchanek.jonsfueltracker_v_1_1.utils

import dev.psuchanek.jonsfueltracker_v_1_1.BuildConfig

//SharedPref
const val FIRST_LAUNCH = "FIRST_LAUNCH"

//ViewHolder types
const val VEHICLE = 1
const val MAINTENANCE = 2
const val TRIP_HISTORY = 3

// Fragment add tab
const val FRAGMENT_ADD_TAB_COUNT = 3
const val FRAGMENT_HISTORY_TAB_COUNT = 2

//Services
const val GET_FROM_SERVICE = BuildConfig.GetHistory
const val POST_TRIP_TO_SERVICE = BuildConfig.PostTrip
const val POST_VEHICLE_TO_SERVICE = BuildConfig.PostVehicle
const val DELETE_TRIP_FROM_SERVICE = BuildConfig.DeleteTrip
const val FUEL_TRACKER_DB_NAME = "fuel_tracker_database"

// OkHttpClient timeouts
const val READ_TIMEOUT = 10L
const val CALL_TIMEOUT = 3L

//Units
const val LITRES_IN_GALLON = 4.546f

const val EMPTY_MILEAGE = 0L


//Line chart
const val RIGHT_AXIS_LABEL_COUNT = 4

//Item decorator
const val DECORATION_SPACING = 5

//Vehicle default names
const val VEHICLE_ONE = "Nissan Micra"
const val VEHICLE_TWO = "Midget"
const val VEHICLE_THREE = "Mercedes Sprinter"
const val UNKNOWN_VEHICLE = "Unknown vehicle"