package com.example.recycleviewwithclicklistener

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button

class Identify : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identify)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Identify"

        val cameraButton = findViewById<Button>(R.id.camera)
        cameraButton.setOnClickListener {
            val intent = Intent(this, CustomCameraActivity::class.java)
            startActivity(intent)
        }

        val galleryButton = findViewById<Button>(R.id.gallery)
        galleryButton.setOnClickListener{
            val intent =
                Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data

            val intent = Intent(this, result_activity::class.java)
            intent.putExtra("image", selectedImageUri.toString())
            startActivity(intent)

        }
    }
}