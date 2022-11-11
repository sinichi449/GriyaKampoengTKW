package net.bagusekasaputra.griyakampoengtkw.ui.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.logEvent
import java.util.concurrent.atomic.AtomicBoolean

class NetworkStatusHelper(
    ctx: Context
): LiveData<NetworkStatus>() {

    private var connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var connectivityManagerCallback: ConnectivityManager.NetworkCallback

    val isOnline = AtomicBoolean(false)

    private fun announceOnline() {
        if (!isOnline.get()) {
            postValue(NetworkStatus.Available)
            isOnline.set(true)
        }
    }

    private fun announceOffline() {
        if (isOnline.get()) {
            postValue(NetworkStatus.Unavailable)
            isOnline.set(false)
        }
    }


    private fun getConnectivityManagerCallback() = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)

            logEvent("Network callback -> onAvailable()")

            val networkCapability = connectivityManager.getNetworkCapabilities(network)
            val hasNetworkConnection = networkCapability?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false

            if (hasNetworkConnection) {
                determineInternetAccess(network)
            } else {
                announceOffline()
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)

            logEvent("Network callback -> onLost()")

            announceOffline()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)

            logEvent("Network callback -> onCapabilitiesChanged()")

            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                determineInternetAccess(network)
            } else {
//                validateNetworkConnections.remove(network)
                announceOffline()
            }
//            announceStatus()
        }
    }

    private fun determineInternetAccess(network: Network) {
        CoroutineScope(Dispatchers.IO).launch {
            if (InternetAvailability.check()) {
                withContext(Dispatchers.Main) {
//                    validateNetworkConnections.add(network)
//                    announceStatus()
                    announceOnline()
                }
            } else {
                announceOffline()
            }
        }
    }

    override fun onActive() {
        super.onActive()

        logEvent("Network status helper is active")

        connectivityManagerCallback = getConnectivityManagerCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, connectivityManagerCallback)
    }

    override fun onInactive() {
        super.onInactive()

        logEvent("Network status helper is inactive")

        connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
    }
}