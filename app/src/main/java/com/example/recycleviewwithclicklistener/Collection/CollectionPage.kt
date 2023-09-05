package com.example.recycleviewwithclicklistener.Collection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recycleviewwithclicklistener.R

class CollectionPage : AppCompatActivity() {

    private var collectionAdapter: CollectionAdapter?=null
    private lateinit var sqLiteHelper: SQLiteHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collection)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "MyCollection"

        // Initialize SQLiteHelper
        sqLiteHelper = SQLiteHelper(this)

        initRecyclerView()
        getMangroves()

        // delete button
        collectionAdapter?.setOnClickDeleteItem {
            deleteMangrove(it.date)
        }

        collectionAdapter?.setOnItemClickListener(object : CollectionAdapter.OnItemClickListener {
            override fun onItemClick(mg: MangroveModel) {
                val intent = Intent(this@CollectionPage, Collection_details::class.java)
                intent.putExtra("name", mg.name)
                intent.putExtra("date", mg.date)
                intent.putExtra("latitude", mg.latitude)
                intent.putExtra("longitude", mg.longitude)
                intent.putExtra("image", mg.image)
                startActivity(intent)
            }
        })

    }

    // delete item function
    private fun deleteMangrove(date:String){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete item?")
        builder.setCancelable(true)
        builder.setPositiveButton("Yes"){dialog,_->
            sqLiteHelper.deleteMangroveDate(date)
            getMangroves()
            dialog.dismiss()
        }
        builder.setNegativeButton("No"){dialog,_->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    // get all mangroves
    private fun getMangroves(){
        val mgList = sqLiteHelper.getAllMangrove()
        Log.e("pppp","${mgList.size}")
        //display data in recycler view
        collectionAdapter?.addItems(mgList)

    }

    // recycler view
    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.collection_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        collectionAdapter = CollectionAdapter(sqLiteHelper)
        recyclerView.adapter = collectionAdapter
    }
}
