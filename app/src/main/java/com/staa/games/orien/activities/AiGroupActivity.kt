package com.staa.games.orien.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.staa.games.orien.R
import com.staa.games.orien.engine.game.hor
import com.staa.games.orien.engine.game.lft
import com.staa.games.orien.engine.game.players.AI
import com.staa.games.orien.engine.game.players.AIDifficulty
import com.staa.games.orien.engine.game.players.Player
import com.staa.games.orien.engine.game.rgt
import com.staa.games.orien.engine.game.ver
import com.staa.games.orien.util.*
import kotlinx.android.synthetic.main.activity_ai_group.*

class AiGroupActivity : BaseActivity()
{
    private val players = ArrayList<Player>()
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_group)

        // add human player
        val sp = getSharedPreferences(defaultSPName, defaultSPMode)
        val name = sp.getString(playerNameSPKey, defaultPlayerName)
        val token = sp.getInt(playerTokenSPKey, defaultPlayerToken)

        players += Player(name, token)
    }

    fun onPlayClick(view: View)
    {
        preparePlayers()
        val i = Intent(this, GameSettingsActivity::class.java)
        i.putExtra("players", players.toTypedArray())
        startActivity(i)
    }


    private fun preparePlayers()
    {
        val count = playerCountSpinner.selectedItem.toString().toInt() - 1
        val rawDifficulty = radioSettingsGroup.checkedRadioButtonId

        val difficulty = when(rawDifficulty)
        {
            ai_difficulty_easy.id -> AIDifficulty.Beginner
            ai_difficulty_normal.id -> AIDifficulty.Regular
            else -> AIDifficulty.Professional
        }

        for (i in 1..count)
        {
            players += AI("Bot $i", nextAvailableToken(), difficulty)
        }
    }


    private fun nextAvailableToken(): Int
    {
        return arrayListOf(ver, hor, rgt, lft).firstOrNull { !players.any { player -> player.token == it } }
               ?: -1
    }
}
