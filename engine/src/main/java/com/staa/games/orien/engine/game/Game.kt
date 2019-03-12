package com.staa.games.orien.engine.game

import com.staa.games.orien.engine.game.moves.Move
import com.staa.games.orien.engine.game.players.AI
import com.staa.games.orien.engine.game.players.Player
import com.staa.games.orien.engine.net.NetworkUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.*
import java.util.*

class Game(val settings: GameSettings, val players: Array<Player>) : Serializable, Cloneable
{
    val board: GameBoard = GameBoard(settings.rowCount)
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
            throw UnsupportedOperationException("Invalid number of users")
        }
    }


    fun newGame(): Game
    {
        if (!moves.empty())
        {
            val g = Game(settings, players)
            g.currentPlayerIndex = currentPlayerIndex
            for (p in players)
            {
                if (p is AI)
                {
                    p.game = g
                }
            }

            return g
        }
        else
        {
            return this
        }
    }


    fun move(point: Point)
    {
        performMove(Move(point, players[currentPlayerIndex].id))
    }

    fun emulateMove(point: Point)
    {
        performMove(Move(point, players[currentPlayerIndex].id))
    }


    private fun performMove(move: Move)
    {
        if (board[move.target] != nul) return

        val player = players[currentPlayerIndex]
        if (move.playerId == player.id)
        {
            moves.push(move)
            board[move.target] = player.token

            updateGameState(player)
        }
    }


    private var lastCallTime = 0L
    fun notifyCurrentPlayerAsync()
    {
        if (PlayNotifier.job == null || (PlayNotifier.job != null && PlayNotifier.job!!.isCompleted))
            GlobalScope.launch(Dispatchers.Main) {
                lastCallTime = System.currentTimeMillis()
                PlayNotifier.job = GlobalScope.async {
                    notifyCurrentPlayer()
                }

                PlayNotifier.job!!.await()

                PlayNotifier.display.refresh()
                val currentPlayer = players[currentPlayerIndex]
                if (currentPlayer is AI || currentPlayer is NetworkUser)
                {
                    notifyCurrentPlayerAsync()
                }
            }
    }

    suspend fun notifyCurrentPlayer()
    {
        if (state == GameState.Running)
        {
            players[currentPlayerIndex].notifyTurn()
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
                it.size >= settings.winSize
            }

    fun undoLastMove()
    {
        if (!moves.isEmpty())
        {
            val move = moves.pop()
            board[move.target] = nul
            switchToPreviousPlayer()
            state = GameState.Running
        }
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


    fun deepCopy(): Game
    {
        var obj: Game? = null
        try
        {
            // Write the object out to a byte array
            val bos = ByteArrayOutputStream()
            val out = ObjectOutputStream(bos)
            out.writeObject(this)
            out.flush()
            out.close()

            // Make an input stream from the byte array and read
            // a copy of the object back in.
            val inputStream = ObjectInputStream(
                    ByteArrayInputStream(bos.toByteArray()))
            obj = inputStream.readObject() as Game
        }
        catch (e: IOException)
        {
            e.printStackTrace()
        }
        catch (cnfe: ClassNotFoundException)
        {
            cnfe.printStackTrace()
        }

        return obj!!
    }
}