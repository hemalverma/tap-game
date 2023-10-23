package com.hvx.tapgame

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.hvx.tapgame.R

class MainActivity : AppCompatActivity() {
    //initialization of variable
    private var clickCount = 0
    var timerTime :Long = 15000
    var timeLeftInMillis :Long=0
    private var timer : CountDownTimer? = null
    private var mInterstitialAd: InterstitialAd? = null


    //instances of class


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)
        val actionBar = this.supportActionBar
        actionBar?.hide()

        Data.mediaPlayerBg?.start()

        val tvTimer : TextView= findViewById(R.id.tv_timer)
        val hs : TextView = findViewById(R.id.tv_hs)
        val scoreTv : TextView = findViewById(R.id.tv_score)
        val clicker:ConstraintLayout = findViewById(R.id.cl_score)
        //game over comps
        val gameOver:ConstraintLayout = findViewById(R.id.cl_game_over)
        val gameOverTime:TextView = findViewById(R.id.tv_game_over_time)
        val gameOverScore:TextView = findViewById(R.id.tv_game_over_score)
        val tvYourScore:TextView = findViewById(R.id.tv_your_score)
        val btnMain : Button = findViewById(R.id.btn_main_menu)
        val btnRestart : Button = findViewById(R.id.btn_restart)



        //high score handling
        hs.text = ("High Score:"+ Data.highScore[Data.selectedTime])

        //timer handling
        tvTimer.text = ("Time: " + Data.selectedTime)
        when(Data.selectedTime){
            "15 sec" ->{ timerTime = 15000}
            "30 sec" ->{ timerTime = 30000}
            "45 sec" ->{ timerTime = 45000}
            "60 sec" ->{ timerTime = 60000}
        }
        timer = object : CountDownTimer(timerTime,10){
            override fun onTick(p0: Long) {
                timeLeftInMillis = p0
                tvTimer.text = ("Time: "+p0/1000+" sec")
                scoreTv.text = (clickCount.toString())
            }

            override fun onFinish() {
                Data.gameStatus = "over"
                clicker.visibility = View.INVISIBLE
                hs.visibility = View.INVISIBLE

                gameOver.visibility = View.VISIBLE
                gameOverTime.text = ("Time: "+Data.selectedTime)
                gameOverScore.text = (clickCount.toString())
                if(clickCount> Data.highScore[Data.selectedTime]!!){
                    tvYourScore.text = ("Highest Score\nYour Score")
                    Data.highScore[Data.selectedTime] = clickCount
                }
            }
        }

        btnMain.setOnClickListener {
            Data.gameStatus = "stopped"
            val intent = Intent(this,Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            //show interstitial ad
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
                loadInterstitialAd()
                if (mInterstitialAd != null){
                    mInterstitialAd?.show(this)
                }
                else{
                    startActivity(intent)
                    finish()
                }
            }
            mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    startActivity(intent)
                    finish()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd?.show(this@MainActivity)
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.")
                    }
                }

                override fun onAdShowedFullScreenContent() {
                    mInterstitialAd = null
                }
            }

        }

        btnRestart.setOnClickListener {
            Data.gameStatus = "stopped"
            clickCount = 0
            timer?.cancel()
            tvTimer.text = ("Time: "+ Data.selectedTime)
            scoreTv.text="Tap"
            hs.text = ("HS:"+ Data.highScore[Data.selectedTime])
            gameOver.visibility = View.INVISIBLE

            clicker.visibility = View.VISIBLE
            hs.visibility = View.VISIBLE

        }

        //starting game
        clicker.setOnClickListener {
            when (Data.gameStatus) {
                "stopped" -> {
                    Data.mediaPlayer?.start()
                    timer?.start()
                    Data.gameStatus="running"
                    clickCount++
                    scoreTv.text = (clickCount.toString())
                }
                "running" -> {
                    Data.mediaPlayer?.start()
                    //click counter
                    clickCount++
                }
                else -> {
                    Toast.makeText(this,"Game Over",Toast.LENGTH_SHORT).show()
                    //game over
                }
            }
        }
        MobileAds.initialize(this) {}
        val mAdView : AdView = findViewById(R.id.adView1)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener(){
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                mAdView.loadAd(adRequest)
            }
        }
        loadInterstitialAd()
    }

    private fun loadInterstitialAd(){
        val adRequest1 = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-4077531402147376/6477504163", adRequest1, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })
    }

    override fun onBackPressed() {
        Data.gameStatus="stopped"
        timer?.cancel()
        super.onBackPressed()

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

}

