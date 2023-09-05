package com.example.recycleviewwithclicklistener.SignUp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.recycleviewwithclicklistener.Login.LogIn
import com.example.recycleviewwithclicklistener.R

class Welcome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val loginBtn: Button = findViewById(R.id.loginBtn)
        val signInBtn: Button = findViewById(R.id.signUpBtn)

        loginBtn.setOnClickListener{
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
        }

        signInBtn.setOnClickListener {
            val intent = Intent(this, signUp::class.java)
            startActivity(intent)
        }
    }

}