package com.example.recycleviewwithclicklistener

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

class CollectionPage : AppCompatActivity() {

    private var collectionAdapter:CollectionAdapter?=null
    private lateinit var sqLiteHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collection)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Your Collection"
        sqLiteHelper = SQLiteHelper(this)

        initRecyclerView()
        getMangroves()

        collectionAdapter?.setOnClickDeleteItem {
            deleteMangrove(it.id)

        }

    }

    private fun deleteMangrove(id:Int){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete item?")
        builder.setCancelable(true)
        builder.setPositiveButton("Yes"){dialog,_->
            sqLiteHelper.deleteMangroveId(id)
            getMangroves()
            dialog.dismiss()
        }
        builder.setNegativeButton("No"){dialog,_->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun getMangroves(){
        val mgList = sqLiteHelper.getAllMangrove()
        Log.e("pppp","${mgList.size}")
        //display data in recycler view
        collectionAdapter?.addItems(mgList)

    }
    //
    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.collection_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        collectionAdapter = CollectionAdapter()
        recyclerView.adapter = collectionAdapter
    }
}