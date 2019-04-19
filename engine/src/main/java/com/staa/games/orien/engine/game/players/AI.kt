package com.staa.games.orien.engine.game.players

import com.staa.games.orien.engine.game.*
import java.io.InvalidObjectException
import kotlin.math.abs

abstract class AI(name: String, token: Int) :
        Player(name, token)
{
    lateinit var game: Game

    override suspend fun notifyTurn()
    {
        val move = getMove()
        game.move(move)
    }


    private fun filter(it: Point, point: Point, token: Int): Boolean
    {
        return when (token)
        {
            hor  -> it.first == point.first &&
                    abs(it.second - point.second) == 1

            ver  -> it.second == point.second &&
                    abs(it.first - point.first) == 1

            rgt  -> it.first + it.second == point.first + point.second &&
                    abs(it.first - point.first) == abs(it.second - point.second) &&
                    abs(it.first - point.first) == 1

            lft  -> abs((it.first + it.second) - (point.first + point.second)) == 2 &&
                    abs(it.first - point.first) == abs(it.second - point.second) &&
                    abs(it.first - point.first) == 1

            else -> throw UnsupportedOperationException("Invalid token")
        }
    }


    protected fun moveWins(point: Point,
                           token: Int,
                           groups: List<ArrayList<Point>>? = null): Boolean
    {
        val mGroups = groups ?: game.board.tokens[token]!!.filter { it ->
            it.any {
                filter(it, point, token)
            }
        }

        return mGroups.sumBy { it.size } + 1 >= game.settings.winSize
    }


    protected fun moveBlocksOpponent(point: Point, initialIndex: Int): Boolean
    {
        // if the other player can play here to win, then it blocks the other player
        game.switchToNextPlayer()

        while (initialIndex != game.currentPlayerIndex)
        {
            val player = game.getCurrentPlayer()

            game.switchToNextPlayer()
            if (moveWins(point, player.token))
            {
                return true
            }
        }
        return false
    }


    protected fun getMoveForInternalSpaces(token: Int,
                                           board: GameBoard,
                                           spaces: Array<Array<Point>>,
                                           useFallback: Boolean = true): Point?
    {
        var fallBack: Point? = null
        for (space in spaces)
        {
            val pieceIndex = space.indexOfFirst { board[it] == token }
            if (pieceIndex > 1)
            {
                return space[pieceIndex - 1]
            }

            if (pieceIndex > -1)
            {
                for (i in pieceIndex + 1 until space.size)
                {
                    if (board[space[i]] != token && i + 1 < space.size && board[space[i + 1]] != token)
                    {
                        if (pieceIndex > 0)
                        {
                            return space[i]
                        }
                        else
                        {
                            fallBack = space[i]
                        }
                    }
                }
            }
        }

        return if (useFallback) fallBack else null
    }


    protected fun getFreeSpaces(board: GameBoard, token: Int): Array<Array<Point>>
    {
        return when (token)
        {
            hor  -> getHorSpaces(board, token)
            ver  -> getVerSpaces(board, token)
            lft  -> getLftSpaces(board, token)
            rgt  -> getRgtSpaces(board, token)
            else -> throw InvalidObjectException("token must be one of ver, hor, lft and rgt")
        }
    }


    private fun getVerSpaces(board: GameBoard, token: Int): Array<Array<Point>>
    {
        val list = arrayListOf<ArrayList<Point>>()
        for (j in 0 until game.settings.rowCount)
        {
            // column
            list.add(arrayListOf())
            for (i in 0 until game.settings.rowCount)
            {
                addToList(board, token, i, j, list)
            }
        }

        return getUsefulArray(list)
    }


    private fun getHorSpaces(board: GameBoard, token: Int): Array<Array<Point>>
    {
        val list = arrayListOf<ArrayList<Point>>()
        for (i in 0 until game.settings.rowCount)
        {
            // column
            list.add(arrayListOf())
            for (j in 0 until game.settings.rowCount)
            {
                addToList(board, token, i, j, list)
            }
        }

        return getUsefulArray(list)
    }


    private fun getRgtSpaces(board: GameBoard, token: Int): Array<Array<Point>>
    {
        val list = arrayListOf<ArrayList<Point>>()
        val rowCount = game.settings.rowCount
        for (i in 0 until rowCount)
        {
            // column
            list.add(arrayListOf())

            var c = 0
            while (i + c in 0 until rowCount)
            {
                addToList(board, token, i + c, c, list)
                c++
            }
        }

        return getUsefulArray(list)
    }


    private fun getLftSpaces(board: GameBoard, token: Int): Array<Array<Point>>
    {
        val list = arrayListOf<ArrayList<Point>>()
        val rowCount = game.settings.rowCount
        for (i in 0 until rowCount)
        {
            // column
            list.add(arrayListOf())

            var c = 0
            while (i - c in 0 until rowCount)
            {
                addToList(board, token, i - c, c, list)
                c++
            }
        }

        return getUsefulArray(list)
    }


    private fun addToList(board: GameBoard,
                          token: Int,
                          i: Int,
                          j: Int,
                          list: ArrayList<ArrayList<Point>>)
    {
        val point = Point(i, j)

        val item = board[point]
        val lastList = list[list.lastIndex]

        if (item != token && item != nul && lastList.any())
        {
            list.add(arrayListOf())
            return
        }

        lastList.add(point)
    }

    private fun getUsefulArray(list: ArrayList<ArrayList<Point>>): Array<Array<Point>>
    {
        if (!list[list.lastIndex].any())
        {
            list.removeAt(list.lastIndex)
        }

        return list
                .filter { it.size >= game.settings.winSize }
                .map { it.toTypedArray() }.toTypedArray()
    }


    abstract fun getMove(): Point
}

enum class AIDifficulty
{
    Beginner,
    Regular,
    Professional
}