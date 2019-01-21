package com.staa.games.orien.network.threads

import java.net.Socket

class ServerThread(private val accept: () -> Socket) : Thread()
{
    lateinit var socket: Socket
    override fun run()
    {
        socket = accept()
    }
}