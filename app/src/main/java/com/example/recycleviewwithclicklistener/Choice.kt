package com.example.recycleviewwithclicklistener

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class Choice : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice)

        // Get the Bitmap object from the intent
        val bitmap = intent.getParcelableExtra<Bitmap>("image")

        val photoImageView: ImageView = findViewById(R.id.image)

        photoImageView.setImageBitmap(bitmap)
    }
}