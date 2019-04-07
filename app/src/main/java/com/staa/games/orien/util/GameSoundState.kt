package com.staa.games.orien.util

import android.media.MediaPlayer

object GameSoundState
{
    var isPlaying: Boolean = false
    lateinit var player: MediaPlayer
    var initialized: Boolean = false
}