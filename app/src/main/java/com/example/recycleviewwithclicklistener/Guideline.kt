package com.example.recycleviewwithclicklistener

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Guideline : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.guideline_app)


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Guideline"

    }
}