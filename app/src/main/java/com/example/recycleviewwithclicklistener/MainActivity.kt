package com.example.recycleviewwithclicklistener

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.example.recycleviewwithclicklistener.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var sqLiteHelper: SQLiteHelper
    lateinit var binding: ActivityMainBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var prefEditor: SharedPreferences.Editor
    lateinit var toggle: ActionBarDrawerToggle

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
                "App to identify Malaysian Mangrove Plants",
                R.drawable.identify_slide
            )
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Home"

        sqLiteHelper = SQLiteHelper(this)

        sharedPreferences = getSharedPreferences("didShowPromt", MODE_PRIVATE)
        prefEditor = sharedPreferences.edit()

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

        val nextTxt: TextView = findViewById(R.id.next)
        val skipTxt: TextView = findViewById(R.id.skip)

        nextTxt.setOnClickListener{
            nextTxt.setOnClickListener{
                if (introSliderViewPage.currentItem+1 < introSliderAdapter.itemCount){
                        introSliderViewPage.currentItem += 1
                }
            }
        }
        skipTxt.setOnClickListener{
            Intent(applicationContext, Identify::class.java).also{
                startActivity(it)
            }
        }
        
        //side drawer
        binding.apply {
            toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open,R.string.close)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)


            sideNavView.setNavigationItemSelectedListener{
                when(it.itemId){
                    R.id.navigation_home->{
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        val intent = Intent(this@MainActivity, MainActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.navigation_identify-> {
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        val intent = Intent(this@MainActivity, Identify::class.java)
                        startActivity(intent)
                        true
                    }

                    R.id.navigation_collection->{
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        val intent = Intent(this@MainActivity, CollectionPage::class.java)
                        startActivity(intent)
                        getMangrove()
                        true
                    }
                    R.id.navigation_contact -> {
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        val intent = Intent(this@MainActivity, AboutUs::class.java)
                        startActivity(intent)
                        true

                    }
                    R.id.navigation_map -> {
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        val intent = Intent(this@MainActivity, MapsActivity::class.java)
                        startActivity(intent)
                        true
                    }

                    R.id.navigation_guideline -> { supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        val intent = Intent(this@MainActivity, Guideline::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.navigation_mangrovedesc-> { supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        val intent = Intent(this@MainActivity, mangroveDescription::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }

        }
    }

    private fun getMangrove(){
        val mgList = sqLiteHelper.getAllMangrove()
        Log.e("pppp","${mgList.size}")
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupIndicators(){
        val indicator = arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
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
                        R.drawable.indicator_inactive)
                )
            }
        }
        val getStarted: TextView = findViewById(R.id.getStart)
        val bottom: LinearLayout = findViewById(R.id.bottom)

        if (index == introSliderAdapter.itemCount -1) {
            getStarted.visibility = View.VISIBLE
            bottom.visibility = View.INVISIBLE

            getStarted.setOnClickListener {
                val intent = Intent(this@MainActivity, Identify::class.java)
                startActivity(intent)
                finish()
            }
        }
        else{
            getStarted.visibility = View.INVISIBLE
            bottom.visibility = View.VISIBLE
        }
    }

}



