package com.example.recycleviewwithclicklistener.Intro

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.example.recycleviewwithclicklistener.MainActivity
import com.example.recycleviewwithclicklistener.R


class IntroActivity :AppCompatActivity() {

    private val introSliderAdapter = IntroSliderAdapter(
        listOf(
            IntroSlider(
                "PlantMangro",
                "Species based Identification on Mangrove Plants",
                R.drawable.logo
            ),
            IntroSlider(
                "MyCollection",
                "Save your own photo collection for future reference",
                R.drawable.collection_slide
            ),
            IntroSlider(
                "Maps",
                "Locations of Mangrove Plants in MyCollection",
                R.drawable.location_slide
            ),
            IntroSlider(
                "Mangrove Plants Description",
                "Description of Malaysian Mangrove Plants Species",
                R.drawable.description
            ),

            IntroSlider(
                "Identification",
                "Feature of identifying Malaysian Mangrove Plants",
                R.drawable.identify_icon
            ),


        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.introslider)
        supportActionBar?.hide()

        val introSliderViewPage: ViewPager2 = findViewById(R.id.introSliderViewPager)
        introSliderViewPage.adapter = introSliderAdapter
        setupIndicators()
        setCurrentIndicator(0)
        introSliderViewPage.registerOnPageChangeCallback(object:
            ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        val Nextbtn: Button = findViewById(R.id.Nextbtn)
        val textSkipIntro: TextView = findViewById(R.id.textSkipIntro)
        Nextbtn.setOnClickListener{
            if (introSliderViewPage.currentItem+1 < introSliderAdapter.itemCount){
                introSliderViewPage.currentItem += 1
            }else{
                Intent(applicationContext, MainActivity::class.java).also{
                    startActivity(it)
                }
            }
        }
        textSkipIntro.setOnClickListener{
            Intent(applicationContext, MainActivity::class.java).also{
                startActivity(it)
            }
        }


    }

    private fun setupIndicators(){
        val indicator = arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT)
        layoutParams.setMargins(8,0,8,0)
        for (i in indicator.indices){
            indicator[i] = ImageView(applicationContext)
            indicator[i].apply{
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            val indicatorContainer: LinearLayout = findViewById(R.id.indicatorsContainer)
            indicatorContainer.addView(indicator[i])
        }
    }
    private fun setCurrentIndicator(index:Int){
        val indicatorContainer: LinearLayout = findViewById(R.id.indicatorsContainer)
        val childCount = indicatorContainer.childCount
        for(i in 0 until childCount){
            val imageView = indicatorContainer[i] as ImageView
            if(i==index){
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            }else{
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }
}
