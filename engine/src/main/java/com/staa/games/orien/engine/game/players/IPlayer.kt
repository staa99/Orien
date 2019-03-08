package com.staa.games.orien.engine.game.players

import java.io.Serializable
import java.util.*

interface IPlayer : Serializable
{
    val id: UUID
    val token: Int
    val name: String

    suspend fun notifyTurn()
}