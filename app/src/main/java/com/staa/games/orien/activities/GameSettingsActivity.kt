package com.staa.games.orien.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.staa.games.orien.R
import com.staa.games.orien.engine.game.GameSettings
import com.staa.games.orien.engine.game.players.Player
import kotlinx.android.synthetic.main.game_settings_layout.*

class GameSettingsActivity : BaseActivity()
{
    private lateinit var players: Array<Player>
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_settings_layout)

        players = intent.getSerializableExtra("players") as Array<Player>
    }


    fun homeClick(view: View)
    {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    fun playClick(view: View)
    {
        val settings: GameSettings
        try
        {
            settings = GameSettings(rowCountEditText.text.toString().trim().toInt(),
                                    winSizeEditText.text.toString().trim().toInt())

            val i = Intent(this, GameActivity::class.java)
            i.putExtra("settings", settings)
            i.putExtra("players", players)
            startActivity(i)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}