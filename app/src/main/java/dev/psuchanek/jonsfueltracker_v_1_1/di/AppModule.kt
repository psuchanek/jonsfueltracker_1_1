package dev.psuchanek.jonsfueltracker_v_1_1.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.psuchanek.jonsfueltracker_v_1_1.repositories.FuelTrackerRepository
import dev.psuchanek.jonsfueltracker_v_1_1.repositories.Repository
import dev.psuchanek.jonsfueltracker_v_1_1.services.db.FuelTrackerDao
import dev.psuchanek.jonsfueltracker_v_1_1.services.db.FuelTrackerDatabase
import dev.psuchanek.jonsfueltracker_v_1_1.services.db.VehicleDao
import dev.psuchanek.jonsfueltracker_v_1_1.services.network.FuelTrackerService
import dev.psuchanek.jonsfueltracker_v_1_1.utils.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMoshi() = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Singleton
    @Provides
    fun provideOkHttpClient() = OkHttpClient.Builder()
        .hostnameVerifier(HostnameVerifier { _, _ -> true })
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .callTimeout(CALL_TIMEOUT, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi) =
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()

    @Singleton
    @Provides
    fun provideFuelTrackerService(retrofit: Retrofit) =
        retrofit.create(FuelTrackerService::class.java)

    @Singleton
    @Provides
    fun provideFuelTrackerDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context, FuelTrackerDatabase::class.java, FUEL_TRACKER_DB_NAME
        ).fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideFuelTrackerDao(database: FuelTrackerDatabase) = database.fuelTrackerDao()

    @Singleton
    @Provides
    fun provideVehicleDao(database: FuelTrackerDatabase) = database.vehicleDao()

    @Singleton
    @Provides
    fun provideRepository(
        apiService: FuelTrackerService,
        vehicleDao: VehicleDao,
        fuelTrackerDao: FuelTrackerDao,
        @ApplicationContext context: Context
    ) =
        FuelTrackerRepository(
            fuelTrackerDao,
            vehicleDao,
            apiService,
            context as Application
        ) as Repository

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putBoolean(FIRST_LAUNCH, true)
        return prefs
    }


}