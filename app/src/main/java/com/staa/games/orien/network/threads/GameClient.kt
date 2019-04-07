package com.staa.games.orien.network.threads

import android.net.nsd.NsdManager
import com.staa.games.orien.network.IGroupView
import com.staa.games.orien.network.Resolver
import com.staa.games.orien.network.Scanner

class GameClient(nsdManager: NsdManager, groupView: IGroupView)
{
    private val scanner: Scanner
    private val clientThread: ClientThread

    init
    {
        val resolver = Resolver(groupView)
        scanner = Scanner(resolver, nsdManager)
        clientThread = ClientThread()
    }

    fun discoverServers()
    {
        scanner.act()
    }
}