package com.example.recycleviewwithclicklistener.Description

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.recycleviewwithclicklistener.R

class DetailedActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Mangrove Plants Description"

        val mangrove = intent.getParcelableExtra<Mangrove>("mangrove")
        if(mangrove != null){
            val title:TextView = findViewById(R.id.detailedText)
            val explanation:TextView = findViewById(R.id.detailedexplanation)
            val commonName:TextView = findViewById(R.id.commonName)
            val imageView:ImageView = findViewById(R.id.detailedImage)
            val background: ImageView = findViewById(R.id.background)

            title.text = mangrove.name
            commonName.text = mangrove.commonName
            explanation.text = mangrove.explanation
            imageView.setImageResource(mangrove.image)
            background.setImageResource(mangrove.image)
        }
    }
}