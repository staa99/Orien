package com.staa.games.orien.network

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log

class Resolver : NsdManager.ResolveListener
{
    var serviceInfo: NsdServiceInfo? = null
    private val resolverTag = "ORIEN_RESOLVER"
    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int)
    {
        // Called when the resolve fails. Use the error code to debug.
        Log.e(resolverTag, "Resolve failed: $errorCode")
    }

    override fun onServiceResolved(serviceInfo: NsdServiceInfo)
    {
        Log.e(resolverTag, "Resolve Succeeded. $serviceInfo")

        if (serviceInfo.serviceName == ActiveNetworkConfig.serviceName)
        {
            Log.d(resolverTag, "Same IP.")
            return
        }

        this.serviceInfo = serviceInfo
    }
}