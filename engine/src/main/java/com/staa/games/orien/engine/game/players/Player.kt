package com.staa.games.orien.engine.game.players

import android.databinding.BaseObservable
import android.databinding.Bindable
import java.util.*

open class Player
(
        @Bindable
        override var name: String,

        @Bindable
        override var token: Int
) : BaseObservable(), IPlayer
{
    override val id = UUID.randomUUID()!!

    /**
     * This does nothing for human players. It is useful for network and AI players.
     * In the case of a network player, it notifies the instance on their device that
     * it is their turn, hence, be allowed to send their move.
     */
    override suspend fun notifyTurn()
    {

    }
}