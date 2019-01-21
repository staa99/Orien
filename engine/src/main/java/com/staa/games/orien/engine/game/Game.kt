package com.staa.games.orien.engine.game

import com.staa.games.orien.engine.game.moves.Move
import com.staa.games.orien.engine.game.players.AI
import com.staa.games.orien.engine.game.players.Player
import java.io.Serializable
import java.util.*

class Game(val settings: GameSettings, val players: Array<Player>) : Serializable, Cloneable
{
    val board: GameBoard = GameBoard(settings.rowCount, settings.rowCount)
    private val moves = Stack<Move>()
    var currentPlayerIndex = 0
    var state = GameState.Running
    var winnerIndex = -1


    fun getCurrentPlayer() =
            players[currentPlayerIndex]

    init
    {
        if (players.size < 2 || players.size > 4)
        {
            throw UnsupportedOperationException("Maximum number of players exceeded")
        }
    }


    fun newGame(): Game
    {
        val g = Game(settings, players)
        for (p in players)
        {
            if (p is AI)
            {
                p.game = g
            }
        }

        return g
    }


    fun move(point: Point)
    {
        performMove(Move(point, players[currentPlayerIndex].id))
    }

    fun emulateMove(point: Point)
    {
        performMove(Move(point, players[currentPlayerIndex].id),
                    emulated = true)
    }


    private fun performMove(move: Move, emulated: Boolean = false)
    {
        if (board[move.target] != nul) return

        val player = players[currentPlayerIndex]
        if (move.playerId == player.id)
        {
            moves.push(move)
            board[move.target] = player.token

            updateGameState(player)

            if (!emulated && state == GameState.Running)
            {
                players[currentPlayerIndex].switchTo()
            }
        }
    }

    private fun updateGameState(player: Player)
    {
        when
        {
            checkWin(player.token) ->
            {
                state = GameState.FinishedWin
                winnerIndex = currentPlayerIndex
            }

            board.isFilled()       ->
            {
                state = GameState.FinishedDraw
                winnerIndex = -1
            }
        }

        switchToNextPlayer()
    }

    private fun checkWin(token: Int) =
            board.tokens[token]!!.any {
                it.size >= getWinSize(token)
            }

    fun getWinSize(token: Int): Int
    {
        return if (token == hor || token == ver)
            settings.winSize
        else
            settings.winSize
    }

    fun undoLastMove()
    {
        val move = moves.pop()
        board[move.target] = nul
        switchToPreviousPlayer()
        state = GameState.Running
    }

    private fun switchToPreviousPlayer()
    {
        if (currentPlayerIndex == 0)
        {
            currentPlayerIndex = players.lastIndex
        }
        else
        {
            currentPlayerIndex--
        }
    }

    fun switchToNextPlayer()
    {
        if (currentPlayerIndex == players.lastIndex)
        {
            currentPlayerIndex = 0
        }
        else
        {
            currentPlayerIndex++
        }
    }
}