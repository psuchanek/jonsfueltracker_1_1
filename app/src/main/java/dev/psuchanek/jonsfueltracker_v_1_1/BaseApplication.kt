package dev.psuchanek.jonsfueltracker_v_1_1

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.psuchanek.jonsfueltracker_v_1_1.repositories.FuelTrackerRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application() {
    @Inject
    lateinit var repository: FuelTrackerRepository

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        GlobalScope.launch {
            repository.getAllTrips()
        }


    }
}