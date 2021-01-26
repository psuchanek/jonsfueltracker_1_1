package dev.psuchanek.jonsfueltracker_v_1_1

import dev.psuchanek.jonsfueltracker_v_1_1.models.Trip

val tripOne = Trip(
    id = 1,
    vehicleId = 1,
    date = "",
    fuelVolume = 0.0f,
    fuelCost = 0.0f,
    tripMileage = 0.0f,
    costPerLitre = 0.0f,
    gasStationName = ""
)

val tripTwo = Trip(
    id = 2,
    vehicleId = 3,
    date = "",
    fuelVolume = 0.0f,
    fuelCost = 0.0f,
    tripMileage = 0.0f,
    costPerLitre = 0.0f,
    gasStationName = ""
)

val tripThree = Trip(
    id = 3,
    vehicleId = 3,
    date = "",
    fuelVolume = 0.0f,
    fuelCost = 0.0f,
    tripMileage = 0.0f,
    costPerLitre = 0.0f,
    gasStationName = ""
)

val listOFTrips = listOf<Trip>(
    tripOne, tripTwo, tripThree
)

