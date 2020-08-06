package com.filipebrandao.revolutassignment.utils.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * BroadcastReceiver which notifies its listeners about the connection status on network state changes
 */
class NetworkChangeReceiver : KoinComponent, BroadcastReceiver() {

    private val networkUtils: NetworkUtils by inject()
    private val listeners = mutableListOf<(Boolean) -> (Unit)>()

    override fun onReceive(context: Context, intent: Intent) {
        // received a network update, check the network state
        checkNetworkStateChange()
    }

    /**
     * Notifies the listeners with the network availability
     */
    private fun checkNetworkStateChange() {
        val isNetworkAvailable = networkUtils.isNetworkAvailable()
        listeners.forEach { it(isNetworkAvailable) }
    }

    /**
     * Adds a new listener to the network changes
     */
    fun addListener(listener: (Boolean) -> (Unit)) = listeners.add(listener)

    /**
     * Removes a network change listener
     */
    fun removeListener(listener: (Boolean) -> (Unit)) = listeners.remove(listener)
}
