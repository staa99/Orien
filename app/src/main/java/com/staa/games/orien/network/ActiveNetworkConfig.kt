package com.staa.games.orien.network

object ActiveNetworkConfig
{
    var roomName: String = "Default game"
    val serviceName: String
        get() = "$NSD_SERVICE_NAME_PREFIX|$roomName"
}