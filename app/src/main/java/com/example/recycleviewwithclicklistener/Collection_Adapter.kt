package com.example.recycleviewwithclicklistener

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CollectionAdapter :RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>(){

    private var mgList:ArrayList<MangroveModel> = ArrayList()
    private var onClickDeleteItem: ((MangroveModel) -> Unit)? = null


    fun addItems(items:ArrayList<MangroveModel>){
        this.mgList = items
        notifyDataSetChanged()
    }

    fun setOnClickDeleteItem(callback:(MangroveModel)->Unit){
        this.onClickDeleteItem = callback
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CollectionViewHolder((
            LayoutInflater.from(parent.context).inflate(R.layout.card_items_mg,parent,false))
    )

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val mg = mgList[position]
        holder.bindView(mg)
        holder.btnDelete.setOnClickListener{onClickDeleteItem?.invoke(mg)}
    }

    override fun getItemCount(): Int {
        return mgList.size
    }

    class CollectionViewHolder(var view:View):RecyclerView.ViewHolder(view){
        private var name =view.findViewById<TextView>(R.id.tvName)
        private var date =view.findViewById<TextView>(R.id.date)
        private var location = view.findViewById<TextView>(R.id.location)
        private var image=view.findViewById<ImageView>(R.id.saved_image)
        var btnDelete =view.findViewById<Button>(R.id.btn_delete)

        fun bindView(mg:MangroveModel) {
            name.text = mg.name
            date.text = mg.date
            val latlng = (mg.latitude).toString() + ", " + (mg.longitude).toString()
            location.text =  latlng

            //val bitmap = convertByteArrayToBitmap(mg.image)
            val imageBitmap = BitmapFactory.decodeByteArray(mg.image, 0, mg.image.size)
            image.setImageBitmap(imageBitmap)


        }

    }
}