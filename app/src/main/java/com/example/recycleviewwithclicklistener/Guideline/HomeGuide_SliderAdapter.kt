package com.example.recycleviewwithclicklistener.Guideline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.recycleviewwithclicklistener.R

class HomeGuide_SliderAdapter(private val HomeSlider: List<HomeGuide_Slider>):
    RecyclerView.Adapter<HomeGuide_SliderAdapter.HomeSliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeSliderViewHolder {
        return HomeSliderViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.slide_item_container_guideline,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeSliderViewHolder, position: Int) {
        val HomeSlider = HomeSlider[position]

        holder.bind(HomeSlider)
    }

    override fun getItemCount(): Int {
        return HomeSlider.size
    }

    inner class HomeSliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageContent = view.findViewById<ImageView>(R.id.imageContent)

        fun bind(HomeSlider: HomeGuide_Slider) {
            imageContent.setImageResource(HomeSlider.imageView)
        }
    }

}


