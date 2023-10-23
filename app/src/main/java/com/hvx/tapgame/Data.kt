package com.hvx.tapgame

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.hvx.tapgame.R
import java.util.HashMap

class Data : Application() {


    companion object {
        lateinit var sharedPref : SharedPreferences
        lateinit var editor: SharedPreferences.Editor
        var gameStatus = "stopped"  //stopped,running,paused,over
        var highScore: MutableMap<String, Int> = HashMap()
        var selectedLocation = 0
        var selectedTime = "15 sec"
        val time = arrayOf("15 sec", "30 sec", "45 sec", "60 sec")
        var mediaPlayer:MediaPlayer? = null
        var mediaPlayerBg:MediaPlayer? = null

    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()
        sharedPref = this.getSharedPreferences("sharedPref",Context.MODE_PRIVATE)
        editor = sharedPref.edit()
        highScore["15 sec"]  = sharedPref.getInt("hs15",0)
        highScore["30 sec"]  = sharedPref.getInt("hs30",0)
        highScore["45 sec"]  = sharedPref.getInt("hs45",0)
        highScore["60 sec"]  = sharedPref.getInt("hs60",0)
        selectedLocation  = sharedPref.getInt("selectedLocation",0)
        mediaPlayer = MediaPlayer.create(this, R.raw.tap_sound)
        mediaPlayerBg = MediaPlayer.create(this,R.raw.bg_music)
        mediaPlayerBg?.isLooping = true


    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        editor.putInt("hs15", highScore["15 sec"]!!)
        editor.putInt("hs30", highScore["30 sec"]!!)
        editor.putInt("hs45", highScore["45 sec"]!!)
        editor.putInt("hs60", highScore["60 sec"]!!)
        editor.putInt("selectedLocation", selectedLocation)
        editor.commit()
        mediaPlayerBg?.stop()

    }

}

