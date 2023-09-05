package com.example.recycleviewwithclicklistener.Guideline

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.example.recycleviewwithclicklistener.R

class HomeDetails :AppCompatActivity() {
    private lateinit var homeguideSlideradapter: HomeGuide_SliderAdapter

    var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.guideline_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "ReadMe"

        val guide = intent.getParcelableExtra<GuidelineModel>("guide")
        if(guide != null) {
            val slider = mutableListOf(
                HomeGuide_Slider(
                    guide.image1
                ),
                HomeGuide_Slider(
                    guide.image2
                )
            )
            if (guide.image3 == 0) {
                count = slider.size -1
            } else {
                slider.add(HomeGuide_Slider(guide.image3!!))
                count = slider.size
            }

            if (guide.image4 == 0) {
                count = slider.size -1
            } else {
                slider.add(HomeGuide_Slider(guide.image4!!))
                count = slider.size
            }

            homeguideSlideradapter = HomeGuide_SliderAdapter(slider)
        }

        val HomeSliderViewPage: ViewPager2 = findViewById(R.id.HomeSliderViewPager)
        HomeSliderViewPage.adapter = homeguideSlideradapter
        setupIndicators()
        setCurrentIndicator(0)

        HomeSliderViewPage.registerOnPageChangeCallback(object:
            ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        val Nextbtn: Button = findViewById(R.id.nextBtn)
        Nextbtn.setOnClickListener{
            if (HomeSliderViewPage.currentItem+1 < homeguideSlideradapter.itemCount){
                HomeSliderViewPage.currentItem += 1
            }
        }

        val activity_name = guide?.activityName
        val openBtn: Button = findViewById(R.id.open)

        val text = "Go to ${guide?.title}"
        val startIndex = text.indexOf("${guide?.title}")

        val spannable = SpannableStringBuilder(text)
        spannable.setSpan(
            StyleSpan(Typeface.ITALIC), // specify the text style (in this case, bold)
            startIndex,
            startIndex + "${guide?.title}".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // specify the spannable flags
        )

        openBtn.text = spannable

        openBtn.setOnClickListener{
            Intent(applicationContext, Class.forName(activity_name)).also{
                startActivity(it)
            }
        }

    }

    private fun setupIndicators(){
        val indicator = arrayOfNulls<ImageView>(homeguideSlideradapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
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
