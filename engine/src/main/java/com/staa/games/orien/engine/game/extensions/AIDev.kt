package com.staa.games.orien.engine.game.extensions

import com.staa.games.orien.engine.game.*
import com.staa.games.orien.engine.game.players.AI
import com.staa.games.orien.engine.game.players.AIDifficulty
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.math.abs

fun AI.bestMove_dev1(game: Game): Pair<Point, Int> =
        runBlocking {
            var depth = 0 //game.players.size
            if (difficulty == AIDifficulty.Professional)
            {
                depth = 1
            }
            else if (difficulty == AIDifficulty.Regular)
            {
                depth = 1
            }

            val availableMoves = game.board
                    .getEmptyPoints()

            val winMove = availableMoves.firstOrNull { moveWins(game, it, token) }

            if (winMove != null)
            {
                return@runBlocking Pair(winMove, 5000)
            }

            when
            {
                availableMoves.size <= 10 -> depth *= 3
                availableMoves.size <= 15 -> depth = 4
            }

            val sortedMoves = availableMoves.map { Pair(it, minimax_dev1(game, it, depth, 1)) }

            return@runBlocking com.staa.games.orien.engine.game.extensions.getTopMoves(sortedMoves)
                    .await()
                    .random()
        }

private fun getTopMoves(scoredMovesPromises: Collection<Pair<Point, Deferred<Int>>>): Deferred<ArrayList<Pair<Point, Int>>> =
        GlobalScope.async {
            val scoredMoves = scoredMovesPromises.map { Pair(it.first, it.second.await()) }
            val sortedMoves = scoredMoves.sortedBy { it.second }
            val topScore = sortedMoves.first().second
            val topMoves = arrayListOf<Pair<Point, Int>>()

            for (it in sortedMoves)
            {
                if (it.second == topScore)
                {
                    topMoves.add(it)
                }
                else break
            }

            topMoves
        }

private fun AI.minimax_dev1(game: Game, node: Point, depth: Int, color: Int): Deferred<Int>
{
    return GlobalScope.async { color * negamax_dev1(game, node, depth, color) }
}

private fun AI.negamax_dev1(game: Game, node: Point, depth: Int, color: Int): Int
{
    val terminalStateCheck = terminal_dev1(game)
    if (terminalStateCheck.first)
    {
        return terminalStateCheck.second
    }

    if (depth == 0)
    {
        return color * scoreState(game)
        //return color * scoreMove(game, node, game.getCurrentPlayer().token)
    }

    var value = -5000

    game.emulateMove(node)

    for (child in game.board.getEmptyPoints())
    {
        var v = -negamax_dev1(game, child, depth - 1, -color)

        /*if (v > 1000)
            v -= 1*/

        if (v > value)
        {
            value = v
        }
    }

    game.undoLastMove()

    return value
}

private fun terminal_dev1(game: Game): Pair<Boolean, Int>
{
    val state = game.state
    return Pair(state != GameState.Running, when (state)
    {
        GameState.Running      -> -1
        GameState.FinishedDraw -> 0
        GameState.FinishedWin  -> 5000
    })
}


private fun scoreState(game: Game): Int
{
    return when (game.state)
    {
        GameState.FinishedWin  -> 5000
        GameState.FinishedDraw -> 0
        GameState.Running      -> heuristicScoreState(game)
    }
}

private fun heuristicScoreState(game: Game): Int
{
    val availableMoves = game.board.getEmptyPoints()

    val token = game.getCurrentPlayer().token

    return availableMoves.sumBy { move ->
        scoreMove(game,
                  move,
                  token)
    }
}

private fun scoreMove(game: Game, point: Point, token: Int): Int
{
    val playerGroups = game.board.tokens[token]!!.filter { moves ->
        moves.any { move ->
            filter(move, point, token)
        }
    }

    val moveWins = moveWins(game, point, token, playerGroups)
    val moveBlocksOpponent = moveBlocksOpponent(game, point, game.currentPlayerIndex)
    val moveWillWinNextTurn = oneMoreToWin(game, point, token, playerGroups)

    return when
    {
        // if the move wins but the human player is lucky,
        // block the move
        moveWins           -> 5000

        moveBlocksOpponent -> 4000

        //moveWillWinNextTurn             -> 3000

        else               ->
            playerGroups.maxBy { it.size }?.size
            ?: 0
    }
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

private fun moveWins(game: Game,
                     point: Point,
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


private fun oneMoreToWin(game: Game,
                         point: Point,
                         token: Int,
                         groups: List<ArrayList<Point>>? = null): Boolean
{
    val mGroups = groups ?: game.board.tokens[token]!!.filter { it ->
        it.any {
            filter(it, point, token)
        }
    }

    return mGroups.sumBy { it.size } + 2 >= game.settings.winSize
}

private fun moveBlocksOpponent(game: Game, point: Point, initialIndex: Int): Boolean
{
    // if the other player needs to play here to win, then it blocks the other player
    // assume they play immediately after
    // chances are later players might not see the imminent win
    // Yet, it does not directly translate to a win
    // for beginners, the game should sometimes skip this step to prevent always blocking.
    game.switchToNextPlayer()

    while (initialIndex != game.currentPlayerIndex)
    {
        val player = game.getCurrentPlayer()

        game.switchToNextPlayer()
        if (moveWins(game, point, player.token))
        {
            return true
        }
    }
    return false
}