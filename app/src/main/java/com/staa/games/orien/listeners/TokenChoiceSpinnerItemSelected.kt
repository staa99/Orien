package com.staa.games.orien.listeners

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import com.staa.games.orien.engine.game.players.Player

class TokenChoiceSpinnerItemSelected(private val tokenChoiceSpinner: Spinner,
                                     var ind: Int,
                                     private val player: Player,
                                     private val tokenList: List<Int>) :
        AdapterView.OnItemSelectedListener
{
    override fun onNothingSelected(parent: AdapterView<*>?)
    {
        treatPosition(ind)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
    {
        ind = position
        treatPosition(ind)
    }


    private fun treatPosition(ind: Int)
    {
        player.token = tokenList[ind]
        tokenChoiceSpinner.setSelection(ind, true)
    }
}