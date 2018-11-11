package com.staa.games.orien.engine.game

import com.staa.games.orien.engine.ai.calculateBestMove
import com.staa.games.orien.engine.game.players.Player
import org.junit.Assert
import org.junit.Test

class BoardTests
{
    @Test
    fun testGameplay()
    {
        val settings = GameSettings(
                rowCount = 3,
                straightWinSize = 3,
                slantWinSize = 3,
                noOfPlayers = 2)

        val players = arrayOf(
                Player("john", ver),
                Player("pete", hor))

        val game = Game(settings, players)


        while (game.state != GameState.finished_win && game.state != GameState.finished_draw)
        {
            val ind = game.currentPlayerIndex
            val outcomes = hashMapOf<Point, Point>()
            val move = calculateBestMove(game, 0, outcomes)
            assert(ind == game.currentPlayerIndex)
            println()
            println()
            println("$move: ${players[ind].name}")
            println(outcomes.filter { it.value.first != 0 }.map {
                "\t${Pair(it.key, it.value.first)}"
            }.joinToString(separator = "\n",
                           prefix = "outcomes: \n"))
            println()
            game.move(move)
            println(game.board)
        }

        Assert.assertEquals(GameState.finished_win, game.state)
    }

    @Test
    fun playCli()
    {
        val settings = GameSettings(
                rowCount = 2,
                straightWinSize = 2,
                slantWinSize = 3,
                noOfPlayers = 2)

        val players = arrayOf(
                Player("pete", hor),
                Player("staa", ver))

        val game = Game(settings, players)

        println(game.board)
        while (game.state != GameState.finished_win && game.state != GameState.finished_draw)
        {
            println()
            println("${game.getCurrentPlayer().name}'s turn: ")
            val move: Point = calculateBestMove(game, 0, hashMapOf())

            /*if (game.getCurrentPlayer() is )
            {
                calculateBestMove(game, 0, hashMapOf())
            }
            else
            {
                /*val line = ""
                val split = line.split(',')
                Point(split[0].toInt(),split[1].toInt())*/
                calculateBestMove(game, 0, hashMapOf())
            }*/

            game.move(move)
            println(game.board)
            println()
        }

        Assert.assertEquals(GameState.finished_draw, game.state)
        //println("${game.players[game.winnerIndex].name} wins")
    }
}