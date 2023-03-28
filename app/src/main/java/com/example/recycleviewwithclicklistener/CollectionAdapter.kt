package com.example.recycleviewwithclicklistener

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CollectionAdapter(private val collectionlist: ArrayList<Collection>)
    : RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>(){

    var onItemClick : ((Collection) -> Unit)? = null

    class CollectionViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val imageView : ImageView = itemView.findViewById(R.id.item_image)
        val item_title: TextView = itemView.findViewById(R.id.item_title)
        val item_explanation: TextView = itemView.findViewById(R.id.item_explanation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_home, parent, false)
        return CollectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val collection = collectionlist[position]
        holder.imageView.setImageBitmap(collection.image)
        holder.item_title.text = collection.name
        holder.item_explanation.text = collection.short

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(collection)

        }
    }

    override fun getItemCount(): Int {
        return collectionlist.size
    }
}