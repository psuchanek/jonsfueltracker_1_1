package dev.psuchanek.jonsfueltracker_v_1_1.services.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.psuchanek.jonsfueltracker_v_1_1.getOrAwaitValue
import dev.psuchanek.jonsfueltracker_v_1_1.listOFTrips
import dev.psuchanek.jonsfueltracker_v_1_1.other.TimePeriod
import dev.psuchanek.jonsfueltracker_v_1_1.other.getTimePeriodTimestamp
import dev.psuchanek.jonsfueltracker_v_1_1.tripFour
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@SmallTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
class FuelTrackerDaoTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var fuelTrackerDao: FuelTrackerDao

    @Inject
    @Named("test_db")
    lateinit var database: FuelTrackerDatabase

    @Before
    fun init() {
        hiltRule.inject()
        fuelTrackerDao = database.fuelTrackerDao()
    }

    @After
    fun tearDown() {
        database.clearAllTables()
        database.close()
    }

    @Test
    fun insertListOfTrips_retrieveAllTrips_expectSuccess() = runBlockingTest {
        //When
        fuelTrackerDao.insertFuelTrackerHistory(listOFTrips)
        val result = fuelTrackerDao.getAllTripsSortedById()

        //Then
        assertThat(result).isNotNull()
        assertThat(result[0].id).isEqualTo(listOFTrips[2].id)

    }

    @Test
    fun insertSingleFuelTrackerTrip_expectSuccess() = runBlockingTest {
        //When
        fuelTrackerDao.insertFuelTrackerHistory(listOFTrips)
        fuelTrackerDao.insertFuelTrackerTrip(tripFour)
        val result = fuelTrackerDao.getAllTripsSortedById()

        //Then
        assertThat(result.size).isEqualTo(listOFTrips.size + 1)


    }

    @Test
    fun insertSameListOfTripsTwice_retrieveList_resultNoDuplicates() = runBlockingTest {
        //Given
        val listToAdd = listOFTrips
        listToAdd[0].date = "dateUpdated"

        //When
        fuelTrackerDao.insertFuelTrackerHistory(listOFTrips)

        fuelTrackerDao.insertFuelTrackerHistory(listToAdd)
        val result = fuelTrackerDao.getAllTripsSortedById()

        //Then
        assertThat(result.size).isEqualTo(listOFTrips.size)
    }

    @Test
    fun insertFuelTrackerHistoryList_thenUpdate_expectSuccess() = runBlockingTest {
        //Given
        val updateTrip = listOFTrips[0]
        updateTrip.date = "dateUpdated"

        //When
        fuelTrackerDao.insertFuelTrackerHistory(listOFTrips)
        fuelTrackerDao.updateTrip(updateTrip)
        val result = fuelTrackerDao.getAllTripsSortedById()

        //Then
        assertThat(result[2].date).isEqualTo(updateTrip.date)
    }

    @Test
    fun insertFuelTrackerTripListWithTimeStamp_retrieveByTimestampRange() = runBlockingTest {

        //When
        fuelTrackerDao.insertFuelTrackerHistory(listOFTrips)
        val result = fuelTrackerDao.getAllTripsByTimestampRange(
            getTimePeriodTimestamp(TimePeriod.ONE_YEAR),
            System.currentTimeMillis()
        )
        //Then
        assertThat(result.size).isEqualTo(2)
    }

    @Test
    fun insertFuelTrackerTripList_retrieveLastInserted() = runBlockingTest {
        //When
        fuelTrackerDao.insertFuelTrackerHistory(listOFTrips)
        val result = fuelTrackerDao.getMostRecentTripRecord()

        //Then
        assertThat(result.id).isEqualTo(listOFTrips[0].id)
    }

    @Test
    fun insertFuelTrackerTripList_getLastKnownMileage() = runBlockingTest {
        //When
        fuelTrackerDao.insertFuelTrackerHistory(listOFTrips)
        val result = fuelTrackerDao.getLastKnownMileage(3)

        //Then
        assertThat(result).isEqualTo(listOFTrips[listOFTrips.lastIndex].currentMileage)
    }

}