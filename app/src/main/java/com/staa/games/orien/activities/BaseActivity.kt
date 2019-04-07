package com.staa.games.orien.activities

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import com.staa.games.orien.R
import com.staa.games.orien.util.GameSoundState
import com.staa.games.orien.util.defaultSPMode
import com.staa.games.orien.util.defaultSPName
import kotlinx.android.synthetic.main.activity_main.*

abstract class BaseActivity : AppCompatActivity()
{
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        sharedPreferences = getSharedPreferences(defaultSPName, defaultSPMode)

        GameSoundState.isPlaying = sharedPreferences.getBoolean("music_playing", true)
        if (!GameSoundState.initialized)
        {
            GameSoundState.player = MediaPlayer.create(applicationContext, R.raw.gentle)
            GameSoundState.initialized = true
        }

        if (GameSoundState.isPlaying)
        {
            GameSoundState.player.isLooping = true
            GameSoundState.player.start()
            GameSoundState.isPlaying = true
        }
    }

    override fun onPause()
    {
        super.onPause()
        sharedPreferences.edit().putBoolean("music_playing", GameSoundState.isPlaying).apply()

        if (GameSoundState.isPlaying)
        {
            GameSoundState.player.pause()
            GameSoundState.isPlaying = false
            if (soundControlBtn != null)
            {
                soundControlBtn.setImageResource(R.drawable.ic_volume_off)
            }
        }
    }


    override fun onResume()
    {
        super.onResume()
        GameSoundState.isPlaying = sharedPreferences.getBoolean("music_playing", true)

        if (GameSoundState.isPlaying)
        {
            GameSoundState.player.start()
            GameSoundState.isPlaying = true

            if (soundControlBtn != null)
            {
                soundControlBtn.setImageResource(R.drawable.ic_volume_up)
            }
        }
    }
}