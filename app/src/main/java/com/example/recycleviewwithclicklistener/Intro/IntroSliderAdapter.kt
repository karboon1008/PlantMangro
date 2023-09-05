package com.example.recycleviewwithclicklistener.Intro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recycleviewwithclicklistener.R

class IntroSliderAdapter (private val introSlider: List<IntroSlider>):
    RecyclerView.Adapter<IntroSliderAdapter.IntroSliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSliderViewHolder {
        return IntroSliderViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.slide_item_container,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: IntroSliderViewHolder, position: Int) {
        holder.bind(introSlider[position])
    }

    override fun getItemCount(): Int {
        return introSlider.size
    }

    inner class IntroSliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textTitle = view.findViewById<TextView>(R.id.textTitle)
        private val description = view.findViewById<TextView>(R.id.textDescription)
        private val imageIcon = view.findViewById<ImageView>(R.id.imageSlideIcon)

        fun bind(introSlider: IntroSlider) {
            textTitle.text = introSlider.title
            description.text = introSlider.description
            imageIcon.setImageResource(introSlider.icon)
        }
    }

}


