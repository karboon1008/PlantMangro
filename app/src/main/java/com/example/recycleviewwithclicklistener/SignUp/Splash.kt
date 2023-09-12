package com.example.recycleviewwithclicklistener.SignUp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.recycleviewwithclicklistener.Identify.CustomCameraActivity
import com.example.recycleviewwithclicklistener.MainActivity
import com.example.recycleviewwithclicklistener.R
import com.google.firebase.auth.FirebaseAuth

class Splash : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        Handler().postDelayed({
            // check if the user is already logged in
            // if logged in before, redirect to MainActivity, if not, redirect to Welcome page
            if (auth.currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, Welcome::class.java))
            }
            finish()
        }, 1000)
    }
}