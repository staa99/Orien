package com.staa.games.orien.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.staa.games.orien.R
import com.staa.games.orien.util.HostOrJoin

class MainActivity : BaseActivity()
{
    private var quitting = false
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed()
    {
        if (!quitting)
        {
            AlertDialog.Builder(this)
                    .setMessage("Do you want to close this game")
                    .setPositiveButton("No") { _, _ ->
                        quitting = false
                    }
                    .setNegativeButton("Yes") { _, _ ->
                        quitting = true
                    }
                    .create()
                    .show()
        }
        else
        {
            super.onBackPressed()
        }
    }


    fun localMultiplayerClick(view: View)
    {
        val i = Intent(this, LocalGroupActivity::class.java)
        startActivity(i)
    }

    fun lanMultiplayerClick(view: View)
    {
        AlertDialog.Builder(this)
                .setMessage("Host or Join game?")
                .setPositiveButton("Host") { _, _ ->
                    val i = Intent(this, LanGroupActivity::class.java)
                    i.putExtra("host", HostOrJoin.Host)
                    startActivity(i)
                }
                .setNegativeButton("Join") { _, _ ->
                    val i = Intent(this, LanGroupActivity::class.java)
                    i.putExtra("host", HostOrJoin.Join)
                    startActivity(i)
                }
                .create()
                .show()
    }
}