package com.example.recycleviewwithclicklistener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AboutUs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "About Us"
    }
}