package dev.psuchanek.jonsfueltracker_v_1_1

import dev.psuchanek.jonsfueltracker_v_1_1.models.LocalFuelTrackerTrip
import dev.psuchanek.jonsfueltracker_v_1_1.utils.TimePeriod
import dev.psuchanek.jonsfueltracker_v_1_1.utils.getTimePeriodTimestamp

val tripOne = LocalFuelTrackerTrip(
    id = 1,
    vehicleId = 1,
    date = "",
    fuelVolume = 0.0f,
    fuelCost = 0.0f,
    tripMileage = 0.0f,
    costPerLitre = 0.0f,
    gasStationName = "",
    timestamp = getTimePeriodTimestamp(TimePeriod.THREE_MONTHS),
    currentMileage = 123
)

val tripTwo = LocalFuelTrackerTrip(
    id = 2,
    vehicleId = 3,
    date = "",
    fuelVolume = 0.0f,
    fuelCost = 0.0f,
    tripMileage = 0.0f,
    costPerLitre = 0.0f,
    gasStationName = "",
    timestamp = getTimePeriodTimestamp(TimePeriod.SIX_MONTHS),
    currentMileage = 125
)

val tripThree = LocalFuelTrackerTrip(
    id = 3,
    vehicleId = 3,
    date = "",
    fuelVolume = 0.0f,
    fuelCost = 0.0f,
    tripMileage = 0.0f,
    costPerLitre = 0.0f,
    gasStationName = "",
    timestamp = getTimePeriodTimestamp(TimePeriod.ONE_YEAR),
    currentMileage = 130
)

val tripFour = LocalFuelTrackerTrip(
    id = 4,
    vehicleId = 0,
    date = "",
    fuelVolume = 0.0f,
    fuelCost = 0.0f,
    tripMileage = 0.0f,
    costPerLitre = 0.0f,
    gasStationName = "",
    timestamp = null,
    currentMileage = 135
)



val listOFTrips = listOf<LocalFuelTrackerTrip>(
    tripOne, tripTwo, tripThree
)

