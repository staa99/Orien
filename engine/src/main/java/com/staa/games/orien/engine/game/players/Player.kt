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

    override fun switchTo()
    {
        // do nothing for human users, the block already prevents them from playing out of turn
        // this serves as a switch for the AI to
    }
}