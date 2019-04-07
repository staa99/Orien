package com.staa.games.orien.network

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo

class Registrar(private val nsdManager: NsdManager, private val mPort: Int) :
        NsdManager.RegistrationListener,
        INsdServiceActor
{
    private var isRegistered = false
    var mServiceName = ActiveNetworkConfig.serviceName

    override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo)
    {
        mServiceName = NsdServiceInfo.serviceName
        isRegistered = true
    }

    override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int)
    {
        isRegistered = false
    }

    override fun onServiceUnregistered(arg0: NsdServiceInfo)
    {
        isRegistered = false
    }

    override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int)
    {
        // registration status has not changed
    }

    override fun act()
    {
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = mServiceName
            serviceType = NSD_SERVICE_TYPE
            port = mPort
        }

        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, this)
    }
}