package com.staa.games.orien.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import com.staa.games.orien.R
import com.staa.games.orien.util.GameSoundState
import kotlinx.android.synthetic.main.activity_main.*

abstract class BaseActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onPause()
    {
        super.onPause()
        intent.putExtra("music", GameSoundState.isPlaying)
        if (GameSoundState.isPlaying)
        {
            GameSoundState.player.pause()
            GameSoundState.isPlaying = false
            soundControlBtn.setImageResource(R.drawable.ic_volume_off)
        }
    }


    override fun onResume()
    {
        super.onResume()
        GameSoundState.isPlaying = intent.getBooleanExtra("music", GameSoundState.isPlaying)

        if (GameSoundState.isPlaying)
        {
            GameSoundState.player.start()
            GameSoundState.isPlaying = true
            soundControlBtn.setImageResource(R.drawable.ic_volume_up)
        }
    }
}