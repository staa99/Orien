package com.staa.games.orien.engine.ai

import com.staa.games.orien.engine.game.*
import kotlin.math.ceil

const val infinity = Int.MAX_VALUE

fun scoreMove(game: Game, point: Point): Int
{
    val player = game.getCurrentPlayer()
    return game.board.getTokenPointScore(player.token,
                                         point,
                                         if (player.token == rgt || player.token == lft)
                                             game.settings.slantWinSize
                                         else
                                             game.settings.straightWinSize
                                        )
}


fun calculateBestMove(game: Game, depth: Int, outcomes: HashMap<Point, Point>, oldSize: Int = game.settings.rowCount * game.settings.rowCount): Point
{
    if (game.state == GameState.finished_win)
        return Point(-infinity, 0)

    if (game.state == GameState.finished_draw)
        return Point(0, 0)


    val emptyPoints = game.board.getEmptyPoints().map { Pair(it, scoreMove(game, it)) }

    val available = (if (oldSize - emptyPoints.size < oldSize / 2.0)
    {
        emptyPoints.sortedByDescending { it.second }.take(ceil(
                oldSize.toDouble() / game.settings.noOfPlayers).toInt())
    }
    else emptyPoints).map { it.first }

    // max depth
    if (depth == game.settings.rowCount * 3)
    {
        return Pair(-available.map { scoreMove(game, it) }.max()!!, 0)
    }

    val size = available.size
    for (move in available)
    {
        game.emulateMove(move)
        val best = calculateBestMove(game, depth + 1, hashMapOf(), size)
        var score = best.first

        if (depth % game.settings.noOfPlayers == 0)
        {
            score *= -1
        }

        outcomes[move] = Point(score, 0)

        game.undoLastMove()
    }

    return if (depth == 0)
    {
        outcomes.maxBy { it.value.first }!!.key
    }
    else
    {
        outcomes.maxBy { it.value.first }!!.value
    }
}

