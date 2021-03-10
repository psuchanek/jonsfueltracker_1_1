package dev.psuchanek.jonsfueltracker_v_1_1.utils

//Services
const val BASE_URL = "http://fuel.tecserv-uk.co.uk/"
const val GET_FROM_SERVICE = "api.php/history"
const val POST_TRIP_TO_SERVICE = "api.php?add_record"
const val POST_VEHICLE_TO_SERVICE = "api.php?add_vehicle"
const val DELETE_TRIP_FROM_SERVICE = "api.php?delete_record"
const val FUEL_TRACKER_DB_NAME = "fuel_tracker_database"

// OkHttpClient timeouts
const val READ_TIMEOUT = 10L
const val CALL_TIMEOUT = 3L

//Units
const val LITRES_IN_GALLON = 4.546f

const val EMPTY_MILEAGE = 0L


//Adapter animation
const val ARROW_ANIM_UP = 1111
const val ARROW_ANIM_DOWN = 2222

//Line chart
const val RIGHT_AXIS_LABEL_COUNT = 4

//Item decorator
const val DECORATION_SPACING = 5