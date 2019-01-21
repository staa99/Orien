package com.staa.games.orien.network

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log

class Scanner(private val resolver: Resolver,
              private val nsdManager: NsdManager) :
        NsdManager.DiscoveryListener,
        INsdServiceActor
{
    private val scannerTag = "ORIEN_SCANNER"

    // Called as soon as service discovery begins.
    override fun onDiscoveryStarted(regType: String)
    {
        Log.d(scannerTag, "Service discovery started")
    }

    override fun onServiceFound(service: NsdServiceInfo)
    {
        // A service was found! Do something with it.
        Log.d(scannerTag, "Service discovery success$service")
        when
        {
            service.serviceType != NSD_SERVICE_TYPE                 ->
                Log.d(scannerTag, "Unknown Service Type: ${service.serviceType}")

            service.serviceName == ActiveNetworkConfig.serviceName  ->
                Log.d(scannerTag, "Same machine: ${ActiveNetworkConfig.serviceName}")

            service.serviceName.startsWith(NSD_SERVICE_NAME_PREFIX) ->
                nsdManager.resolveService(service, resolver)
        }
    }

    override fun onServiceLost(service: NsdServiceInfo)
    {
        // When the network service is no longer available.
        // Internal bookkeeping code goes here.
        Log.e(scannerTag, "service lost: $service")
    }

    override fun onDiscoveryStopped(serviceType: String)
    {
        Log.i(scannerTag, "Discovery stopped: $serviceType")
    }

    override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int)
    {
        Log.e(scannerTag, "Discovery failed: Error code:$errorCode")
        nsdManager.stopServiceDiscovery(this)
    }

    override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int)
    {
        Log.e(scannerTag, "Discovery failed: Error code:$errorCode")
        nsdManager.stopServiceDiscovery(this)
    }

    override fun act()
    {
        nsdManager.discoverServices(NSD_SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, this)
    }
}