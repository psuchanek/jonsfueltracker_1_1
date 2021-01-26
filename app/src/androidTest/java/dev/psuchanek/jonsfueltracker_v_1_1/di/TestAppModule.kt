package dev.psuchanek.jonsfueltracker_v_1_1.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.psuchanek.jonsfueltracker_v_1_1.services.db.FuelTrackerDatabase
import javax.inject.Named

@Module
@InstallIn(ApplicationComponent::class)
object TestAppModule {

    @Provides
    @Named("test_db")
    fun provideInMemoryDatabase(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(context, FuelTrackerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
}