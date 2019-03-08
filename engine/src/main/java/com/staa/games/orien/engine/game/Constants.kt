package com.staa.games.orien.engine.game

import kotlinx.coroutines.Deferred

typealias Point = kotlin.Pair<Int, Int>

const val nul = 0
const val hor = 1
const val ver = 2
const val rgt = 3
const val lft = 4

object PlayNotifier
{
    lateinit var display: IGameDisplay
    var job: Deferred<Unit>? = null
}