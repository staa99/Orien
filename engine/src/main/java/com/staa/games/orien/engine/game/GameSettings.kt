package com.staa.games.orien.engine.game

import java.io.Serializable

data class GameSettings
(
        // since it is square, the row count = column count
        val rowCount: Int,
        val winSize: Int
) : Serializable