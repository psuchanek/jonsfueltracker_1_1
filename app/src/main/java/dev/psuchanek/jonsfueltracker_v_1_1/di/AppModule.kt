package dev.psuchanek.jonsfueltracker_v_1_1.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.psuchanek.jonsfueltracker_v_1_1.services.db.FuelTrackerDatabase
import dev.psuchanek.jonsfueltracker_v_1_1.services.network.FuelTrackerService
import dev.psuchanek.jonsfueltracker_v_1_1.utils.BASE_URL
import dev.psuchanek.jonsfueltracker_v_1_1.utils.CALL_TIMEOUT
import dev.psuchanek.jonsfueltracker_v_1_1.utils.FUEL_TRACKER_DB_NAME
import dev.psuchanek.jonsfueltracker_v_1_1.utils.READ_TIMEOUT
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

}