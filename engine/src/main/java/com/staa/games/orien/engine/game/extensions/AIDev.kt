package com.staa.games.orien.engine.game.extensions

import com.staa.games.orien.engine.game.*
import com.staa.games.orien.engine.game.players.AI
import com.staa.games.orien.engine.game.players.AIDifficulty
import kotlin.math.abs

fun AI.bestMove_dev1(): Pair<Point, Int>
{
    var depth = 1 //game.players.size
    if (difficulty == AIDifficulty.Professional)
    {
        depth *= 3
    }
    else if (difficulty == AIDifficulty.Regular)
    {
        depth *= 2
    }

    val sortedMoves = game.board
            .getEmptyPoints()
            .map { Pair(it, minimax_dev1(it, depth, 1)) }

    return com.staa.games.orien.engine.game.extensions.getTopMoves(sortedMoves).random()
}

private fun getTopMoves(scoredMoves: Collection<Pair<Point, Int>>): ArrayList<Pair<Point, Int>>
{
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

    return topMoves
}

private fun AI.minimax_dev1(node: Point, depth: Int, color: Int): Int
{
    return color * negamax_dev1(node, depth, color)
}

private fun AI.negamax_dev1(node: Point, depth: Int, color: Int): Int
{
    val terminalStateCheck = terminal_dev1(node)
    if (terminalStateCheck.first)
    {
        return terminalStateCheck.second
    }

    if (depth == 0)
    {
        return color * scoreState(game)
        //return color * scoreMove(game, node, game.getCurrentPlayer().token)
    }

    var value = -2000

    game.emulateMove(node)

    for (child in game.board.getEmptyPoints())
    {
        var v = -negamax_dev1(child, depth - 1, -color)

        if (v > 1000)
            v -= 1

        if (v > value)
        {
            value = v
        }
    }

    game.undoLastMove()
    return value
}

private fun AI.terminal_dev1(node: Point): Pair<Boolean, Int>
{
    game.emulateMove(node)
    val state = game.state
    game.undoLastMove()
    return Pair(state != GameState.Running, when (state)
    {
        GameState.Running      -> -1
        GameState.FinishedDraw -> 0
        GameState.FinishedWin  -> 2000
    })
}


private fun AI.bestMove_dev2()
{

}


private fun scoreState(game: Game): Int
{
    return when (game.state)
    {
        GameState.FinishedWin  -> 2000
        GameState.FinishedDraw -> 0
        GameState.Running      -> heuristicScoreState(game)
    }
}

private fun heuristicScoreState(game: Game): Int
{
    return game.board.getEmptyPoints().sumBy { scoreMove(game, it, game.getCurrentPlayer().token) }
}

private fun scoreMove(game: Game, point: Point, token: Int): Int
{
    val playerGroups = game.board.tokens[token]!!.filter { moves ->
        moves.any { move ->
            filter(move, point, token)
        }
    }

    val player = game.getCurrentPlayer()
    val luckyBeginner = player is AI && player.difficulty == AIDifficulty.Beginner && Math.random() <= 0.1
    val luckyRegular = player is AI && player.difficulty == AIDifficulty.Regular && Math.random() <= 0.05

    val moveWins = moveWins(game, point, token, playerGroups)
    val moveBlocksOpponent = moveBlocksOpponent(game, point, game.currentPlayerIndex)

    return when
    {
        // if the move wins but the human player is lucky,
        // block the move
        moveWins &&
        luckyBeginner                   -> -2000

        moveWins                        -> 2000
        // if the move blocks a win but the human player is lucky,
        // give the move a high score but possible to be randomized among potential `close to wins`
        moveBlocksOpponent &&
        (luckyBeginner || luckyRegular) -> 50

        moveBlocksOpponent              -> 100
        else                            ->
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

    return mGroups.sumBy { it.size } + 1 >= if (token == rgt || token == lft)
    {
        game.settings.winSize
    }
    else
    {
        game.settings.winSize
    }
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