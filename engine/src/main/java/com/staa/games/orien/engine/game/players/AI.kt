package com.staa.games.orien.engine.game.players

import com.staa.games.orien.engine.game.Game
import com.staa.games.orien.engine.game.extensions.bestMove_dev1

class AI(name: String, token: Int, val difficulty: AIDifficulty) :
        Player(name, token)
{
    lateinit var game: Game

    override suspend fun notifyTurn()
    {
        val ind = game.currentPlayerIndex
        val move = bestMove_dev1(game.deepCopy())
        assert(ind == game.currentPlayerIndex)
        {
            System.err.println("Move $move by $name caused an issue when calculating the best move")
            System.err.println(game.board)
        }
        game.move(move.first)

        game.notifyCurrentPlayer()
    }
}

enum class AIDifficulty
{
    Beginner,
    Regular,
    Professional
}