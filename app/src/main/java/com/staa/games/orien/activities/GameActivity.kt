package com.staa.games.orien.activities

import android.os.Bundle
import com.staa.games.orien.engine.game.Game
import com.staa.games.orien.engine.game.GameSettings
import com.staa.games.orien.engine.game.PlayNotifier
import com.staa.games.orien.engine.game.players.AI
import com.staa.games.orien.engine.game.players.Player
import com.staa.games.orien.views.GameView

class GameActivity : BaseActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val settings = intent.extras["settings"] as GameSettings
        val players = intent.extras["players"] as Array<Player>
        val canUndo = intent.getBooleanExtra("canUndo", true)
        val gameView = GameView(this)

        PlayNotifier.display = gameView
        val game = Game(settings, players)
        for (p in players)
        {
            if (p is AI)
            {
                p.game = game
            }
        }

        gameView.setGame(game, canUndo)
        setContentView(gameView)
    }
}