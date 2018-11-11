package com.staa.games.orien.activities

import android.databinding.ObservableArrayList
import android.os.Bundle
import com.staa.games.orien.R
import com.staa.games.orien.adapters.TokenSpinnerAdapter
import com.staa.games.orien.engine.game.hor
import com.staa.games.orien.engine.game.lft
import com.staa.games.orien.engine.game.players.Player
import com.staa.games.orien.engine.game.rgt
import com.staa.games.orien.engine.game.ver
import com.staa.games.orien.listeners.TokenChoiceSpinnerItemSelected
import com.staa.games.orien.util.*
import kotlinx.android.synthetic.main.user_settings_layout.*

class UserSettingsActivity : BaseActivity()
{
    private val tokenChoices = ObservableArrayList<Int>()
    private lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_settings_layout)
        val sp = getSharedPreferences(defaultSPName, defaultSPMode)

        val name = sp.getString(playerNameSPKey, defaultPlayerName)
        val token = sp.getInt(playerTokenSPKey, defaultPlayerToken)

        val player = Player(name, token)

        tokenChoices.add(hor)
        tokenChoices.add(ver)
        tokenChoices.add(rgt)
        tokenChoices.add(lft)
        tokenChoiceSpinner.adapter = TokenSpinnerAdapter(this, tokenChoices)

        tokenChoiceSpinner.onItemSelectedListener = TokenChoiceSpinnerItemSelected(
                tokenChoiceSpinner, 0, player, tokenChoices)
    }


}