package com.maximshuhman.bsuirschedule.domain

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


sealed class NetworkStatus {
    object Available : NetworkStatus()
    object Unavailable : NetworkStatus()
}

class NetworkStatusTracker(
    private val connectivityManager: ConnectivityManager
) {


    val networkStatus = callbackFlow {
        val networkStatusCallback = object : ConnectivityManager.NetworkCallback() {


            override fun onUnavailable() {
                trySend(NetworkStatus.Unavailable).isSuccess
            }

            override fun onAvailable(network: Network) {
                trySend(NetworkStatus.Available).isSuccess
            }

           /* override fun onLost(network: Network) {
                trySend(NetworkStatus.Unavailable).isSuccess
            }*/
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkStatusCallback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkStatusCallback)
        }
    }


    fun getCurrentNetworkStatus(): NetworkStatus {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }

        return if (capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
            NetworkStatus.Available
        } else {
            NetworkStatus.Unavailable
        }
    }
}

suspend inline fun Flow<NetworkStatus>.collect(
    crossinline onUnavailable: suspend () -> Unit,
    crossinline onAvailable: suspend () -> Unit,
) {
    this.collect { status ->
        when (status) {
            NetworkStatus.Unavailable -> onUnavailable()
            NetworkStatus.Available -> onAvailable()
        }
    }
}