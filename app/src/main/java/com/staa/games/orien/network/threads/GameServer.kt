package com.staa.games.orien.network.threads

import android.net.nsd.NsdManager
import com.staa.games.orien.network.INsdServiceActor
import com.staa.games.orien.network.Registrar
import java.net.ServerSocket

class GameServer(count: Int, nsdManager: NsdManager)
{
    private val registrar: INsdServiceActor
    private var threads: Array<ServerThread>
    private val serverSocket = ServerSocket().apply { bind(null) }
    private var lock = Any()

    init
    {
        if (count !in 2..4)
        {
            throw UnsupportedOperationException("The room size must be in the range 2..4")
        }

        val threadList = arrayListOf<ServerThread>()
        for (i in 1 until count)
        {
            threadList.add(ServerThread(this::accept))
        }

        threads = threadList.toTypedArray()
        registrar = Registrar(nsdManager, serverSocket.localPort)
        registrar.act()
    }

    private fun accept() =
            synchronized(lock)
            {
                serverSocket.accept()
            }!!

    fun release()
    {
        for (t in threads)
        {
            t.running = false
            serverSocket.close()
        }
    }
}