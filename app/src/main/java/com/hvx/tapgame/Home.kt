package com.hvx.tapgame

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.hvx.tapgame.R

class Home : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    //instances of class

    //variable declaration

    private var highScore : TextView? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_home)
        val actionBar = this.supportActionBar
        actionBar?.hide()



        val spinner : Spinner = findViewById(R.id.spinner)
        val startBtn : Button= findViewById(R.id.btn_start_game)
        highScore = findViewById(R.id.tv_high_score)

        Data.mediaPlayerBg?.start()


        //spinner handling
        spinner.onItemSelectedListener = this
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Data.time)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter
        spinner.setSelection(Data.selectedLocation)

        //high score handling
        highScore?.text = ("High Score : "+ Data.highScore[Data.selectedTime])

        //btn handling
        startBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        MobileAds.initialize(this) {}
        val mAdView : AdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener(){
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                mAdView.loadAd(adRequest)
            }
        }

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Data.selectedTime = Data.time[p2]
        Data.selectedLocation=p2
        //high score handling
        highScore?.text = ("High Score : "+ Data.highScore[Data.selectedTime])
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Toast.makeText(this, "Nothing Selected", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        if (Data.mediaPlayerBg?.isPlaying == true)
        Data.mediaPlayerBg?.pause()

    }

    override fun onResume() {
        super.onResume()
        if (Data.mediaPlayerBg?.isPlaying==false)
        Data.mediaPlayerBg?.start()
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit")
            .setMessage("Are you sure want to quit the game..")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                finish()
            }
            .setNegativeButton("No"){ dialog, _ ->
                dialog.cancel()
            }
        builder.create().show()


    }

}