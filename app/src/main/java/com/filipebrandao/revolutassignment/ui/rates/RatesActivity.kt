package com.filipebrandao.revolutassignment.ui.rates

import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import com.filipebrandao.revolutassignment.R
import com.filipebrandao.revolutassignment.ui.BaseActivity
import com.filipebrandao.revolutassignment.utils.bindView
import com.filipebrandao.revolutassignment.utils.network.NetworkChangeReceiver

class RatesActivity : BaseActivity() {
    companion object {
        private const val TAG = "RatesActivity"
        private const val CONNECTIVITY_CHANGE_INTENT_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    }

    private val noNetworkView: View by bindView(R.id.noNetworkView)
    private val networkChangeReceiver = NetworkChangeReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(findViewById(R.id.toolbar))

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RatesFragment.newInstance())
                .commitNow()
        }
    }

    override fun onResume() {
        super.onResume()
        registerNetworkChangesReceiver()
    }

    private fun registerNetworkChangesReceiver() {
        val filter = IntentFilter().apply {
            addAction(CONNECTIVITY_CHANGE_INTENT_ACTION)
        }
        registerReceiver(networkChangeReceiver, filter)
        networkChangeReceiver.addListener(::onNetworkChanged)
    }

    override fun onPause() {
        super.onPause()
        networkChangeReceiver.removeListener(::onNetworkChanged)
        unregisterReceiver(networkChangeReceiver)
        noNetworkView.visibility = View.GONE
    }

    private fun onNetworkChanged(hasNetworkConnection: Boolean) {
        logger.d(TAG, "Network connection is now %s", hasNetworkConnection)

        if (!hasNetworkConnection) {
            noNetworkView.visibility = View.VISIBLE
        } else {
            noNetworkView.visibility = View.GONE
        }
    }
}
