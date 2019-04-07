package com.staa.games.orien.network

import android.net.nsd.NsdServiceInfo

interface IGroupView
{
    fun addServer(serviceInfo: NsdServiceInfo)
    fun connect(serviceInfo: NsdServiceInfo)
}