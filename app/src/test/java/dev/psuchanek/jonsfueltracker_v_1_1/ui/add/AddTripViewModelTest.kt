package dev.psuchanek.jonsfueltracker_v_1_1.ui.add

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import dev.psuchanek.jonsfueltracker_v_1_1.MainCoroutineRule
import dev.psuchanek.jonsfueltracker_v_1_1.getOrAwaitValue
import dev.psuchanek.jonsfueltracker_v_1_1.other.Status
import dev.psuchanek.jonsfueltracker_v_1_1.repositories.FakeFuelTrackerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AddTripViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    //Subject under test
    lateinit var addTripViewModel: AddTripViewModel

    @Before
    fun init() {
        addTripViewModel = AddTripViewModel(FakeFuelTrackerRepository())
    }


    @Test
    fun `submit trip when missing fields and return error`() = runBlockingTest {
        //When
        addTripViewModel.submitTrip(
            date = "",
            stationName = "",
            vehicleId = 0,
            price = "",
            ppl = "",
            fuelVolume = "",
            tripMileage = "",
            totalMileage = ""
        )

        val result = addTripViewModel.submitTripStatus.getOrAwaitValue()

        //Then
        assertThat(result).isEqualTo(Status.ERROR)
    }

    @Test
    fun `submit trip with vehicle id out of bound and return error`() = runBlockingTest {
        //When
        addTripViewModel.submitTrip(
            date = "01/01/2021",
            stationName = "London",
            vehicleId = 5,
            price = "23.45",
            ppl = "1.123",
            fuelVolume = "20.2",
            tripMileage = "343",
            totalMileage = "1234"
        )

        val result = addTripViewModel.submitTripStatus.getOrAwaitValue()

        //Then
        assertThat(result).isEqualTo(Status.ERROR)
    }

    @Test
    fun `submit trip with three missing fields and return error`() = runBlockingTest {
        //When
        addTripViewModel.submitTrip(
            date = "01/01/2021",
            stationName = "",
            vehicleId = 2,
            price = "",
            ppl = "1.123",
            fuelVolume = "20.2",
            tripMileage = "",
            totalMileage = "1234"
        )

        val result = addTripViewModel.submitTripStatus.getOrAwaitValue()

        //Then
        assertThat(result).isEqualTo(Status.ERROR)
    }

    @Test
    fun `submit trip with all fields and return success`() = runBlockingTest {
        //When
        addTripViewModel.submitTrip(
            date = "01/01/2021",
            stationName = "London",
            vehicleId = 2,
            price = "24.5",
            ppl = "1.123",
            fuelVolume = "20.2",
            tripMileage = "343",
            totalMileage = "1234"
        )

        val result = addTripViewModel.submitTripStatus.getOrAwaitValue()

        //Then
        assertThat(result).isEqualTo(Status.SUCCESS)
    }

}