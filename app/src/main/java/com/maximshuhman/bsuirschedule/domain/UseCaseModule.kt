package com.maximshuhman.bsuirschedule.domain

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    var networkStatusTracker : NetworkStatusTracker? = null

    @Provides
    fun provideNetworkStatusFlow() : Flow<NetworkStatus> = networkStatusTracker!!.networkStatus


    @Provides
    fun provideNetworkStatusTracker() = networkStatusTracker!!

}