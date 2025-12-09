package com.maximshuhman.bsuirschedule.data

import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.maximshuhman.bsuirschedule.data.repositories.ScheduleNetworkSourceImpl
import com.maximshuhman.bsuirschedule.data.sources.AppDatabase
import com.maximshuhman.bsuirschedule.data.sources.IISService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.internal.platform.PlatformRegistry.applicationContext
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

    @Provides
    @Singleton
    fun provideDatabase(): AppDatabase = Room.databaseBuilder(
        applicationContext!!,
        AppDatabase::class.java, "database-name"
    ).build()

    @Provides
    fun provideUserDao() = provideDatabase().groupsDAO()

    @Provides
    fun provideEmployeeDao() = provideDatabase().employeeDAO()

    @Provides
    fun provideScheduleDao() = provideDatabase().scheduleDAO()

    @Provides
    fun provideSettingsDao() = provideDatabase().settingsDAO()
}