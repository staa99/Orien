package com.staa.games.orien.network.br

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager
import com.staa.games.orien.activities.LanGroupActivity

class GameBR
(
        private val manager: WifiP2pManager,
        private val channel: WifiP2pManager.Channel,
        private val activity: LanGroupActivity
) : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        when (intent.action)
        {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION       ->
            {
                // Determine if Wifi P2P mode is enabled or not, alert
                // the Activity.
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                activity.isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED

                if (!activity.isWifiP2pEnabled)
                {
                    activity.resetData()
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION       ->
            {

                // The peer list has changed! We should probably do something about
                // that.


            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION  ->
            {

                // Connection state changed! We should probably do something about
                // that.

            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION ->
            {

            }
        }
    }
}