package dev.psuchanek.jonsfueltracker_v_1_1.ui.add

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import dev.psuchanek.jonsfueltracker_v_1_1.MainCoroutineRule
import dev.psuchanek.jonsfueltracker_v_1_1.getOrAwaitValue
import dev.psuchanek.jonsfueltracker_v_1_1.repositories.FakeFuelTrackerRepository
import dev.psuchanek.jonsfueltracker_v_1_1.utils.Status
import dev.psuchanek.jonsfueltracker_v_1_1.utils.TimePeriod
import dev.psuchanek.jonsfueltracker_v_1_1.utils.getTimePeriodTimestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddTripViewModelTest {

    //Subject under test
    private lateinit var viewModel: AddTripViewModel

    //fake repo
    private lateinit var repository: FakeFuelTrackerRepository


    @get:Rule
    var instantExecutorRulw = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun init() {
        viewModel = AddTripViewModel(FakeFuelTrackerRepository())
    }

    @Test
    fun `test insert trip when all fields are not empty`() {

        //when
        viewModel.insertTrip(
            stationName = "test",
            timestamp = getTimePeriodTimestamp(TimePeriod.SIX_MONTHS),
            vehicleId = 3,
            price = "123",
            ppl = "123",
            fuelVolume = "123",
            tripMileage = "123",
            totalMileage = "123"
        )
        val result = viewModel.submitTripStatus.getOrAwaitValue()

        //Then
        assertThat(result).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun `test insert trip when vehicleID is out of bound`() {

        //when
        viewModel.insertTrip(
            stationName = "test",
            timestamp = getTimePeriodTimestamp(TimePeriod.SIX_MONTHS),
            vehicleId = 0,
            price = "123",
            ppl = "123",
            fuelVolume = "123",
            tripMileage = "123",
            totalMileage = "123"
        )
        val result = viewModel.submitTripStatus.getOrAwaitValue()

        //Then
        assertThat(result).isEqualTo(Status.ERROR)
    }

}