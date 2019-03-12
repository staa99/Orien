package com.staa.games.orien.activities

import android.content.Context
import android.net.nsd.NsdManager
import android.os.Bundle
import android.view.View
import com.staa.games.orien.R
import com.staa.games.orien.util.HostOrJoin
import kotlinx.android.synthetic.main.activity_lan_group.*

class LanGroupActivity : BaseActivity()
{
    private lateinit var nsdManager: NsdManager
    private lateinit var hostOrJoin: HostOrJoin

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lan_group)
        nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager
        hostOrJoin = HostOrJoin.valueOf(intent.getStringExtra("host"))
    }

    override fun onStart()
    {
        super.onStart()
        when (hostOrJoin)
        {
            HostOrJoin.Host ->
            {
                hostOrJoinTitleName.text = resources.getString(R.string.lan_multiplayer_host)
                displayGameInitiationSettings()
            }

            HostOrJoin.Join ->
            {
                hostOrJoinTitleName.text = resources.getString(R.string.lan_multiplayer_join)
                displayGroupSearchList()
            }
        }
    }

    private fun displayGroupSearchList()
    {

    }

    private fun displayGameInitiationSettings()
    {
        hostGroupLayout.visibility = View.VISIBLE

    }

    fun resetData()
    {

    }
}
