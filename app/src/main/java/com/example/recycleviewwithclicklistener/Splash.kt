package com.example.recycleviewwithclicklistener

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        supportActionBar?.hide()

        val sharedPreferences = getSharedPreferences("Intro", Context.MODE_PRIVATE)
        val isIntronShown = sharedPreferences.getBoolean("isIntroShown", false)

        Handler().postDelayed({
            // Start Main activity if the introslide has been shown before
            if(isIntronShown){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else {
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            }
        }, 1000)

        // Save the value indicating the intro slider has been shown
        val editor = sharedPreferences.edit()
        editor.putBoolean("isIntroShown", true)
        editor.apply()
    }
}