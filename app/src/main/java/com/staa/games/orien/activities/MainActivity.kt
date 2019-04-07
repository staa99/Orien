package com.staa.games.orien.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.staa.games.orien.R
import com.staa.games.orien.util.GameSoundState
import com.staa.games.orien.util.HostOrJoin
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onBackPressed() =
            AlertDialog.Builder(this)
                    .setMessage("Do you want to close this game")
                    .setPositiveButton("No") { _, _ ->

                    }
                    .setNegativeButton("Yes") { _, _ ->
                        super.onBackPressed()
                    }
                    .create()
                    .show()


    fun localMultiplayerClick(view: View)
    {
        val i = Intent(this, LocalGroupActivity::class.java)
        startActivity(i)
    }

    fun lanMultiplayerClick(view: View)
    {
        displayYetToImplement()
        return
        AlertDialog.Builder(this)
                .setMessage("Host or Join game?")
                .setPositiveButton("Host") { _, _ ->
                    val i = Intent(this, LanGroupActivity::class.java)
                    i.putExtra("host", HostOrJoin.Host.name)
                    startActivity(i)
                }
                .setNegativeButton("Join") { _, _ ->
                    val i = Intent(this, LanGroupActivity::class.java)
                    i.putExtra("host", HostOrJoin.Join.name)
                    startActivity(i)
                }
                .create()
                .show()
    }

    private fun displayYetToImplement()
    {
        AlertDialog.Builder(this)
                .setMessage("Feature scheduled for future release")
                .create()
                .show()
    }

    fun aiPlayClick(view: View)
    {
        displayYetToImplement()
        return
        val i = Intent(this, AiGroupActivity::class.java)
        startActivity(i)
    }

    fun onCreditsClick(view: View)
    {
        val i = Intent(this, CreditsActivity::class.java)
        startActivity(i)
    }

    fun onSettingsClick(view: View)
    {
        val i = Intent(this, UserSettingsActivity::class.java)
        startActivity(i)
    }

    fun onSoundClick(view: View)
    {
        if (GameSoundState.isPlaying)
        {
            GameSoundState.player.pause()
            GameSoundState.isPlaying = false
            soundControlBtn.setImageResource(R.drawable.ic_volume_off)
        }
        else
        {
            GameSoundState.player.start()
            GameSoundState.isPlaying = true
            soundControlBtn.setImageResource(R.drawable.ic_volume_up)
        }
    }
}