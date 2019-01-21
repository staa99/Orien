package com.staa.games.orien.network.threads

import android.content.Context
import android.net.wifi.WifiManager

class GameClient(private val context: Context)
{
    fun discoverPeers()
    {
        val mgr = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    }
}