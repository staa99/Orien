package com.staa.games.orien.activities

import android.content.Intent
import android.databinding.ObservableArrayList
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.staa.games.orien.R
import com.staa.games.orien.adapters.AppRecyclerViewAdapter.Companion.localGroupRecyclerViewAdapter
import com.staa.games.orien.engine.game.hor
import com.staa.games.orien.engine.game.players.Player
import com.staa.games.orien.engine.game.ver
import kotlinx.android.synthetic.main.activity_local_group.*

class LocalGroupActivity : AppCompatActivity()
{
    private val players = ObservableArrayList<Player>()
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_group)

        players.add(Player("Player 1", hor))
        players.add(Player("Player 2", ver))
        initRecyclerView()
    }

    private fun initRecyclerView()
    {
        localMultiplayerPlayers.layoutManager = LinearLayoutManager(this,
                                                                    LinearLayoutManager.VERTICAL,
                                                                    false)

        localMultiplayerPlayers.adapter = localGroupRecyclerViewAdapter(players)
    }


    fun onClickAddPlayer(view: View)
    {
        if (players.size < 4)
        {
            players.add(Player("Player ${players.size}", 0))
        }
        else
        {
            Toast.makeText(this, "max is 4", Toast.LENGTH_SHORT).show()
        }
    }

    fun onClickRemovePlayer(view: View)
    {
        if (players.size > 2)
        {
            players.removeAt(players.lastIndex)
        }
        else
        {
            Toast.makeText(this, "min is 2", Toast.LENGTH_SHORT).show()
        }
    }

    fun onPlayClick(view: View)
    {
        if (validate())
        {
            val i = Intent(this, GameSettingsActivity::class.java)
            i.putExtra("players", players.toTypedArray())
            startActivity(i)
        }
        else
        {
            Toast.makeText(this, "Check players", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validate() =
            players.asSequence().groupBy { it.token }.all { it.value.size == 1 } &&
                    players.asSequence().groupBy { it.name }.all { it.value.size == 1 }
}