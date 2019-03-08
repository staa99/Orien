package com.staa.games.orien.engine.game

import com.staa.games.orien.engine.game.players.AI
import com.staa.games.orien.engine.game.players.AIDifficulty
import com.staa.games.orien.engine.game.players.Player
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class BoardTests
{
    @Test
    fun testGamePlay()
    {
        val settings = GameSettings(
                rowCount = 5,
                winSize = 3)

        val john = AI("john", ver, AIDifficulty.Beginner)
        val pete = AI("pete", hor, AIDifficulty.Professional)
        val players = arrayOf(john as Player, pete)

        // simulate 100 games, assert that the number of wins of the pro is greater than that of a beginner
        var drawCount = 0
        var proWinCount = 0
        var beginnerWinCount = 0

        for (i in 1..3)
        {
            println("Game $i started")

            val game = Game(settings, players)
            john.game = game
            pete.game = game

            PlayNotifier.display = object : IGameDisplay
            {
                override fun refresh()
                {
                    println(game.board)
                }
            }

            while (game.state == GameState.Running)
            {
                notifyPlayers(game)
            }


            println(game.board)
            when (game.state)
            {
                GameState.FinishedDraw ->
                {
                    drawCount++
                    println("The game ended as a draw")
                }

                GameState.FinishedWin  ->
                {
                    when (game.winnerIndex)
                    {
                        0 ->
                        {
                            println("Beginner wins")
                            beginnerWinCount++
                        }

                        1 ->
                        {
                            println("Professional wins")
                            proWinCount++
                        }
                    }
                }
            }
        }

        Assert.assertTrue("pro wins: $proWinCount\nbeginner wins: $beginnerWinCount\ndraws: $drawCount",
                          beginnerWinCount < proWinCount)
    }


    private fun notifyPlayers(game: Game)
    {
        game.notifyCurrentPlayerAsync()

        runBlocking {
            PlayNotifier.job?.await()
        }
    }


    @Test
    fun playCli()
    {
        val settings = GameSettings(
                rowCount = 5,
                winSize = 3)

        val pete = AI("pete", hor, AIDifficulty.Professional)
        val players = arrayOf(
                pete,
                Player("staa", ver))


        val game = Game(settings, players)
        pete.game = game

        PlayNotifier.display = object : IGameDisplay
        {
            override fun refresh()
            {
                println(game.board)
            }
        }

        println(game.board)
        while (game.state != GameState.FinishedWin && game.state != GameState.FinishedDraw)
        {
            println()

            val currentPlayer = game.getCurrentPlayer()
            println("${currentPlayer.name}'s turn: ")

            notifyPlayers(game)


            if (currentPlayer !is AI)
            {
                val move = Point(-1, -1)
                println(game.board)

                /**
                 * Play by modifying in debug mode
                 */
                if (move == Point(-1, -1))
                {
                    throw UnsupportedOperationException("Human player skipping")
                }

                game.move(move)
                game.notifyCurrentPlayerAsync()
            }

            println(game.board)
            println()
        }

        Assert.assertEquals(GameState.FinishedDraw, game.state)
        //println("${game.players[game.winnerIndex].name} wins")
    }
}