package io.golos.data.network_state

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject

class NetworkStateCheckerImpl
@Inject
constructor(private val appContext: Context) : NetworkStateChecker {

    private val connectivityManager: ConnectivityManager by lazy {
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override val isConnected: Boolean
        @SuppressLint("MissingPermission")
        get() = connectivityManager.activeNetworkInfo?.isConnected ?: false
}