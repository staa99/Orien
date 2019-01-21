package com.staa.games.orien.util

import android.content.Context
import com.staa.games.orien.engine.game.ver

enum class HostOrJoin
{
    Host, Join
}

const val defaultSPName = "player_settings"
const val defaultSPMode = Context.MODE_PRIVATE

const val playerNameSPKey = "player_name"
const val defaultPlayerName = "New Player"
const val playerTokenSPKey = "player_token"
const val defaultPlayerToken = ver
const val userSettingsReturnKey = "settings_returnUrl"