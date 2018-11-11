package com.staa.games.orien.activities

import android.os.Bundle
import com.staa.games.orien.engine.game.Game
import com.staa.games.orien.views.GameView

class GameActivity : BaseActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val game = intent.extras["game"] as Game
        val gameView = GameView(this)

        gameView.setGame(game)
        setContentView(gameView)
    }
}