package com.staa.games.orien.activities

import android.content.Context
import android.content.Intent
import android.databinding.ObservableArrayList
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.staa.games.orien.R
import com.staa.games.orien.adapters.LanGroupAdapter
import com.staa.games.orien.network.ActiveNetworkConfig
import com.staa.games.orien.network.IGroupView
import com.staa.games.orien.network.threads.GameClient
import com.staa.games.orien.network.threads.GameServer
import com.staa.games.orien.util.HostOrJoin
import kotlinx.android.synthetic.main.activity_lan_group.*
import kotlin.random.Random

class LanGroupActivity : BaseActivity(), IGroupView
{
    override fun connect(serviceInfo: NsdServiceInfo)
    {
        Log.i("ORIEN", "Join game")
    }

    override fun addServer(serviceInfo: NsdServiceInfo)
    {
        rooms.add(serviceInfo)
    }

    private lateinit var nsdManager: NsdManager
    private lateinit var hostOrJoin: HostOrJoin
    private val rooms = ObservableArrayList<NsdServiceInfo>()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lan_group)
        nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager
        hostOrJoin = HostOrJoin.valueOf(intent.getStringExtra("host"))

        groupListRecyclerView.adapter = LanGroupAdapter(rooms, ::connect)
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
        joinGroupLayout.visibility = View.VISIBLE
        ActiveNetworkConfig.client = GameClient(nsdManager, this)
        ActiveNetworkConfig.client!!.discoverServers()
    }

    private fun displayGameInitiationSettings()
    {
        hostGroupLayout.visibility = View.VISIBLE
    }

    fun hostGameBtnClick(view: View)
    {
        val count = playerCountEditText.text.toString().toIntOrNull() ?: 0
        if (count !in 2..4)
        {
            Toast.makeText(this,
                           getString(R.string.host_game_out_of_bounds_error_message),
                           Toast.LENGTH_SHORT).show()
            return
        }

        ActiveNetworkConfig.roomName = groupNameEditText.text.toString()
        if (ActiveNetworkConfig.roomName.isEmpty())
        {
            ActiveNetworkConfig.roomName = "Room " + Random(0).nextInt(1111, 10000)
        }

        ActiveNetworkConfig.server = GameServer(count, nsdManager)

        val i = Intent(this, LanRoomListActivity::class.java)
        startActivity(i)
    }
}