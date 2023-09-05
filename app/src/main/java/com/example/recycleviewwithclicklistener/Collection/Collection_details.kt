package com.example.recycleviewwithclicklistener.Collection

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.recycleviewwithclicklistener.MapsActivity
import com.example.recycleviewwithclicklistener.R

class Collection_details : AppCompatActivity() {

    private lateinit var coImage : ImageView
    private lateinit var coName: TextView
    private lateinit var coDate: TextView
    private lateinit var coLocation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_details)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Collection Details"

        coImage = findViewById(R.id.collection_image)
        coDate = findViewById(R.id.coDateDetails)
        coLocation = findViewById(R.id.coLocationDetails)
        coName = findViewById(R.id.coNameDetails)

        //receive uri from custom camera
        val intent = intent

        val image = intent.getByteArrayExtra("image")
        val name = intent.getStringExtra("name")
        val date = intent.getStringExtra("date")
        val longitude = intent.getStringExtra("longitude")
        val latitude = intent.getStringExtra("latitude")
        val location = latitude + ", " + longitude

        //val bitmap = convertByteArrayToBitmap(mg.image)
        val bitmap_image = BitmapFactory.decodeByteArray(image, 0, image!!.size)

        coImage.setImageBitmap(bitmap_image)
        coName.text = name
        coDate.text = date
        coLocation.text = location

        coLocation.setOnClickListener{
            val maps_intent =  Intent(this@Collection_details, MapsActivity::class.java)
            maps_intent.putExtra("latlng",location)
            startActivity(maps_intent)
        }
    }


}