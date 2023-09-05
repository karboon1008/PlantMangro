package com.example.recycleviewwithclicklistener.Guideline

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recycleviewwithclicklistener.R
import java.util.ArrayList

class Guideline : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var glList: ArrayList<GuidelineModel>
    private lateinit var GuidelineAdapater: GuidelineAdapater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.guideline_app)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "ReadMe"

        init()
        GuidelineAdapater.onItemClick = {
            val intent = Intent(this, HomeDetails::class.java)
            intent.putExtra("guide", it)
            startActivity(intent)
        }
    }

    private fun init() {
        recyclerView = findViewById(R.id.guideline_recycleview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        glList = ArrayList()
        addDataToList()
        GuidelineAdapater = GuidelineAdapater(glList)
        recyclerView.adapter = GuidelineAdapater
    }

    private fun addDataToList() {
        glList.add(GuidelineModel("com.example.recycleviewwithclicklistener.MainActivity","Home", "Guide on using the Home screen",R.drawable.readme_home, R.drawable.readme_home2,null,null))
        glList.add(GuidelineModel("com.example.recycleviewwithclicklistener.Description.mangroveDescription","Mangrove Plants Description", "Guide on using the Mangrove Plants Description screen",R.drawable.readme_desc,R.drawable.readme_desc2,R.drawable.readme_desc3,null))
        glList.add(GuidelineModel("com.example.recycleviewwithclicklistener.Collection.Identify","Identify", "Guide on using the Identify screen",R.drawable.readme_identify,R.drawable.readme_identify2,R.drawable.readme_identify3,R.drawable.readme_identify4))
        glList.add(GuidelineModel("com.example.recycleviewwithclicklistener.Collection.CollectionPage","MyCollection", "Guide on using the MyCollection screen", R.drawable.readme_collection,R.drawable.readme_collection2,null,null))
        glList.add(GuidelineModel("com.example.recycleviewwithclicklistener.MapsActivity","Maps", "Guide on using the Maps screen",R.drawable.readme_maps,R.drawable.readme_maps2,null,null))
    }
}