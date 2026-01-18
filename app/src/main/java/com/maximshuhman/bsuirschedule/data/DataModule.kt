package com.maximshuhman.bsuirschedule.data

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.maximshuhman.bsuirschedule.DataBase.MigrationCallback
import com.maximshuhman.bsuirschedule.data.repositories.ScheduleNetworkSourceImpl
import com.maximshuhman.bsuirschedule.data.repositories.SettingsRepository
import com.maximshuhman.bsuirschedule.data.sources.AppDatabase
import com.maximshuhman.bsuirschedule.data.sources.IISService
import com.maximshuhman.bsuirschedule.data.sources.SettingsDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    val BASE_URL = "https://iis.bsuir.by/api/v1/"

    val networkJson = Json { ignoreUnknownKeys = true }


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Set connect timeout to 30 seconds
        .readTimeout(20, TimeUnit.SECONDS)    // Set read timeout to 20 seconds
        .writeTimeout(25, TimeUnit.SECONDS)   // Set write timeout to 25 seconds
        .build()


    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): IISService =
        retrofit.create(IISService::class.java)

    @Provides
    @Singleton
    fun provideUserRepository(apiService: IISService): ScheduleSource =
        ScheduleNetworkSourceImpl(apiService)

    @Volatile
    private var INSTANCE: AppDatabase? = null

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "RoomSchedule"
            )
                .addCallback(MigrationCallback(appContext))
                .build().also { INSTANCE = it }
        }
    }

    @Provides
    fun provideGroupsDao(database: AppDatabase) = database.groupsDAO()

    @Provides
    fun provideSubgroupDao(database: AppDatabase) = database.subgroupDAO()

    @Provides
    fun provideEmployeeDao(database: AppDatabase) = database.employeeDAO()

    @Provides
    fun provideScheduleDao(database: AppDatabase) = database.scheduleDAO()

    @Provides
    fun provideSettingsDao(database: AppDatabase) = database.settingsDAO()

    @Provides
    @Singleton
    fun provideSettingsRepository(settingsDAO: SettingsDAO): SettingsRepository =
        SettingsRepository(settingsDAO)
}