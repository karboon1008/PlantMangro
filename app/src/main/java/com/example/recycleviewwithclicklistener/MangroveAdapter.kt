package com.example.recycleviewwithclicklistener

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MangroveAdapter(private val mangrovelist: ArrayList<Mangrove>)
    : RecyclerView.Adapter<MangroveAdapter.MangroveViewHolder>(){

    var onItemClick : ((Mangrove) -> Unit)? = null

    class MangroveViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val imageView : ImageView = itemView.findViewById(R.id.item_image)
        val item_title: TextView = itemView.findViewById(R.id.item_title)
        val item_explanation: TextView = itemView.findViewById(R.id.item_explanation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangroveViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_home, parent, false)
        return MangroveViewHolder(view)
    }

    override fun onBindViewHolder(holder: MangroveViewHolder, position: Int) {
        val mangrove = mangrovelist[position]
        holder.imageView.setImageResource(mangrove.image)
        holder.item_title.text = mangrove.name
        holder.item_explanation.text = mangrove.short

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(mangrove)

        }
    }

    override fun getItemCount(): Int {
        return mangrovelist.size
    }
}