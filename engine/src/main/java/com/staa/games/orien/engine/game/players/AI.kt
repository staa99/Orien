package com.staa.games.orien.engine.game.players

import com.staa.games.orien.engine.ai.calculateBestMove
import com.staa.games.orien.engine.game.Game
import com.staa.games.orien.engine.game.Point

class AI(name: String, token: Int, val game: Game, val difficulty: AIDifficulty) :
        Player(name, token)
{
    override fun switchTo()
    {
        val ind = game.currentPlayerIndex
        val outcomes = hashMapOf<Point, Point>()
        val move = calculateBestMove(game, 0, outcomes)
        assert(ind == game.currentPlayerIndex)
        println()
        println()
        println("$move: $name")
        println(outcomes.filter { it.value.first != 0 }.map {
            "\t${Pair(it.key, it.value.first)}"
        }.joinToString(separator = "\n",
                       prefix = "outcomes: \n"))
        println()
        game.move(move)
        println(game.board)
    }
}

enum class AIDifficulty
{
    beginner,
    player,
    veteran
}