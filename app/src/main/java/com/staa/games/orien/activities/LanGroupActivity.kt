package com.staa.games.orien.activities

import android.content.Context
import android.net.nsd.NsdManager
import android.os.Bundle
import com.staa.games.orien.R
import com.staa.games.orien.util.HostOrJoin

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

            }

            HostOrJoin.Join ->
            {

            }
        }
    }

    fun resetData()
    {

    }
}
