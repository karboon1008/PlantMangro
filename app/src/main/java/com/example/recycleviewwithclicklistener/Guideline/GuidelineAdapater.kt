package com.example.recycleviewwithclicklistener.Guideline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recycleviewwithclicklistener.R
import kotlin.collections.ArrayList


class GuidelineAdapater(private val glList: ArrayList<GuidelineModel>)
    : RecyclerView.Adapter<GuidelineAdapater.GuidelineViewHolder>() {

    var onItemClick : ((GuidelineModel) -> Unit)? = null

    class GuidelineViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val item_title: TextView = itemView.findViewById(R.id.title)
        val item_desc: TextView = itemView.findViewById(R.id.desc)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuidelineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.guideline_cardview, parent, false)
        return GuidelineViewHolder(view)

    }

    override fun onBindViewHolder(holder: GuidelineViewHolder, position: Int) {
        val gl = glList[position]
        //holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.pink))
        holder.item_title.text = gl.title
        holder.item_desc.text = gl.desc

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(gl)
        }
    }

    override fun getItemCount(): Int {
        return glList.size
    }
}