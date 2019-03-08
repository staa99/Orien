package com.staa.games.orien.engine.net

import com.staa.games.orien.engine.game.players.Player


class NetworkUser(name: String, token: Int) : Player(name, token)
{
    override suspend fun notifyTurn()
    {
        return super.notifyTurn()
    }
}