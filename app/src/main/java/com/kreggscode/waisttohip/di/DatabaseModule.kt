package com.kreggscode.waisttohip.di

import android.content.Context
import androidx.room.Room
import com.kreggscode.waisttohip.data.local.AppDatabase
import com.kreggscode.waisttohip.data.local.MealDao
import com.kreggscode.waisttohip.data.local.MeasurementDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "waisttohip_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideMealDao(database: AppDatabase): MealDao {
        return database.mealDao()
    }
    
    @Provides
    @Singleton
    fun provideMeasurementDao(database: AppDatabase): MeasurementDao {
        return database.measurementDao()
    }
}
