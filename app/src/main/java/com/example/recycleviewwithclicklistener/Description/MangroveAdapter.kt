package com.example.recycleviewwithclicklistener.Description

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recycleviewwithclicklistener.R
import java.util.*
import kotlin.collections.ArrayList

class MangroveAdapter(private val mangrovelist: ArrayList<Mangrove>)
    : RecyclerView.Adapter<MangroveAdapter.MangroveViewHolder>(),Filterable{

    var onItemClick : ((Mangrove) -> Unit)? = null

    class MangroveViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val imageView : ImageView = itemView.findViewById(R.id.item_image)
        val item_title: TextView = itemView.findViewById(R.id.item_title)
        val item_explanation: TextView = itemView.findViewById(R.id.item_explanation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangroveViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mangrove_desc_cardview, parent, false)
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

    private var filteredDataList: List<Mangrove> = mangrovelist

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint.toString().lowercase(Locale.getDefault()).trim()
                filteredDataList = if (query.isEmpty()) {
                    mangrovelist
                } else {
                    mangrovelist.filter { it.name.lowercase(Locale.getDefault()).contains(query) }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredDataList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredDataList = results?.values as List<Mangrove>
                notifyDataSetChanged()
            }
        }
    }
    var filteredMangrovelist: MutableList<Mangrove> = mutableListOf()

    fun updateList(newList: MutableList<Mangrove>) {
        mangrovelist.clear()
        mangrovelist.addAll(newList)
        filteredMangrovelist.clear()
        filteredMangrovelist.addAll(newList)
        notifyDataSetChanged()
    }
}