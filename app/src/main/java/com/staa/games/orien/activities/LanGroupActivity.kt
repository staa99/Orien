package com.staa.games.orien.activities

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import com.staa.games.orien.R
import com.staa.games.orien.network.br.GameBR

class LanGroupActivity : BaseActivity()
{
    private val intentFilter = IntentFilter()
    private lateinit var mChannel: WifiP2pManager.Channel
    private lateinit var mManager: WifiP2pManager
    private lateinit var receiver: GameBR
    var isWifiP2pEnabled = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lan_group)


        // create intent filter for broadcasts
        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        mManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        mChannel = mManager.initialize(applicationContext, mainLooper, null)
    }

    override fun onResume()
    {
        super.onResume()
        receiver = GameBR(mManager, mChannel, this)
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause()
    {
        super.onPause()
        unregisterReceiver(receiver)
    }

    fun resetData()
    {

    }
}
