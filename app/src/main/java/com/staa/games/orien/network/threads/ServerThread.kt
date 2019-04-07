package com.staa.games.orien.network.threads

import java.net.Socket

class ServerThread(private val accept: () -> Socket) : Thread()
{
    var running = false
    lateinit var socket: Socket
    override fun run()
    {
        running = true
        socket = accept()
    }
}