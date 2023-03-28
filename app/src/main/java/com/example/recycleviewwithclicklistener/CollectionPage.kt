package com.example.recycleviewwithclicklistener

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

class CollectionPage : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var collectionlist: ArrayList<Collection>
    private lateinit var collectionAdapter:CollectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collection)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Your Collection"

        val collection = intent.getParcelableExtra<Collection>("collection")
        if(collection != null){
            val title: TextView = findViewById(R.id.detailedText)
            val explanation: TextView = findViewById(R.id.detailedexplanation)
            val commonName: TextView = findViewById(R.id.commonName)
            val imageView: ImageView = findViewById(R.id.detailedImage)
            val background: ImageView = findViewById(R.id.background)

            title.text = collection.name
            commonName.text = collection.commonName
            explanation.text = collection.explanation
        }
        init_collection()
    }
    private fun init_collection() {
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        collectionlist = ArrayList()

        collectionAdapter = CollectionAdapter(collectionlist)
        recyclerView.adapter = collectionAdapter

        collectionAdapter.onItemClick = {
            val intent = Intent(this, CollectionPage::class.java)
            intent.putExtra("collection", it)
            startActivity(intent)
        }
    }

}