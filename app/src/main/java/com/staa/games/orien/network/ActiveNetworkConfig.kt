package com.staa.games.orien.network

import com.staa.games.orien.network.threads.GameClient
import com.staa.games.orien.network.threads.GameServer

object ActiveNetworkConfig
{
    var roomName: String = "Default game"
    val serviceName: String
        get() = "$NSD_SERVICE_NAME_PREFIX|$roomName"
    var server: GameServer? = null
    var client: GameClient? = null
}