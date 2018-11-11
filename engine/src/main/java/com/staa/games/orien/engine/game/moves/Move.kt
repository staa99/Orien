package com.staa.games.orien.engine.game.moves

import com.staa.games.orien.engine.game.Point
import java.io.Serializable
import java.util.*

data class Move
(
        val target: Point,
        val playerId: UUID
) : Serializable