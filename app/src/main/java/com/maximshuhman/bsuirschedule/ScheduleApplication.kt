package com.maximshuhman.bsuirschedule

import android.app.Application
import android.net.ConnectivityManager
import com.maximshuhman.bsuirschedule.domain.NetworkStatusTracker
import com.maximshuhman.bsuirschedule.domain.UseCaseModule
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ScheduleApplication: Application() {

    lateinit var networkTracker : NetworkStatusTracker

    override fun onCreate() {
        super.onCreate()

        networkTracker = NetworkStatusTracker(this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager)

        UseCaseModule.networkStatusTracker = networkTracker

    }
}