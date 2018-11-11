package com.staa.games.orien.util

import android.content.Context
import com.staa.games.orien.engine.game.ver

enum class HostOrJoin
{
    Host, Join
}

val defaultSPName = "player_settings"
val defaultSPMode = Context.MODE_PRIVATE

val playerNameSPKey = "player_name"
val defaultPlayerName = "orien master"
val playerTokenSPKey = "player_token"
val defaultPlayerToken = ver