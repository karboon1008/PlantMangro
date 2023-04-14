package com.example.recycleviewwithclicklistener

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.example.recycleviewwithclicklistener.databinding.ActivityMainBinding
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var sqLiteHelper: SQLiteHelper
    lateinit var binding: ActivityMainBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var prefEditor: SharedPreferences.Editor
    lateinit var toggle: ActionBarDrawerToggle

    private val introSliderAdapter = IntroSliderAdapter(
        listOf(
            IntroSlider(
                "Take Photo",
                "Take a photo of mangrove plant",
                R.drawable.take_photo
            ),
            IntroSlider(
                "Prediction",
                "Instant mangrove plant species prediction",
                R.drawable.classification
            ),
            IntroSlider(
                "Collection",
                "Save your findings in collection for future reference",
                R.drawable.collection
            )
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sqLiteHelper = SQLiteHelper(this)

        sharedPreferences = getSharedPreferences("didShowPromt", MODE_PRIVATE)
        prefEditor = sharedPreferences.edit()

        showCameraPromt()

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

        val cameraButton = findViewById<Button>(R.id.camera)
        cameraButton.setOnClickListener {
            val intent = Intent(this, CustomCameraActivity::class.java)
            startActivity(intent)
        }

        val galleryButton = findViewById<Button>(R.id.gallery)
        galleryButton.setOnClickListener{
            val intent =
                Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
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
                    R.id.navigation_collection->{
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        val intent = Intent(this@MainActivity, CollectionPage::class.java)
                        startActivity(intent)
                        getMangrove()
                        true
                    }
                    R.id.navigation_contact -> {
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        val intent = Intent(this@MainActivity, Contact::class.java)
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

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    true
                }
                R.id.navigation_collection -> {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    val intent = Intent(this, CollectionPage::class.java)
                    startActivity(intent)
                    getMangrove()
                    true
                }
                R.id.navigation_contact -> {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    val intent = Intent(this, Contact::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_map -> {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_guideline -> {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    val intent = Intent(this, Guideline::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }

    private fun getMangrove(){
        val mgList = sqLiteHelper.getAllMangrove()
        Log.e("pppp","${mgList.size}")
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data

            val intent = Intent(this, result_activity::class.java)
            intent.putExtra("image", selectedImageUri.toString())
            startActivity(intent)

        }
    }
    private fun showCameraPromt(){
        if(!sharedPreferences.getBoolean("didShowPromt", false)){
            TapTargetView.showFor(this, TapTarget.forView(binding.camera, "Custom Camera","Open the camera to take a picture of mangrove plant leaf")
                .tintTarget(false)
                .outerCircleColor(R.color.purple_200)
                .textColor(R.color.white),
                object : TapTargetView.Listener() {
                    override fun onTargetClick(view: TapTargetView?) {
                        super.onTargetClick(view)
                        showGalleryPrompt()
                    }

                }
            )
        }

    }
    private fun showGalleryPrompt(){
        TapTargetView.showFor(this, TapTarget.forView(binding.gallery, "Gallery","Open the gallery and select one picture")
            .tintTarget(false)
            .outerCircleColor(R.color.purple_200)
            .textColor(R.color.white),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    showCollectionPrompt()
                }
            }
        )
    }

    private fun showCollectionPrompt(){
        TapTargetView.showFor(this, TapTarget.forView(binding.navView.findViewById(R.id.navigation_collection), "Collection","Check your collection")
            .tintTarget(false)
            .outerCircleColor(R.color.purple_200)
            .textColor(R.color.white),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    showMapsPrompt()
                }
            }
        )
    }

    private fun showMapsPrompt(){
        TapTargetView.showFor(this, TapTarget.forView(binding.navView.findViewById(R.id.navigation_map), "Maps","Check your finding's location in all around the world")
            .tintTarget(false)
            .outerCircleColor(R.color.purple_200)
            .textColor(R.color.white),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    showCallPrompt()
                }
            }
        )
    }

    private fun showCallPrompt(){
        TapTargetView.showFor(this, TapTarget.forView(binding.navView.findViewById(R.id.navigation_contact), "Contact","Contact us when you have any question!")
            .tintTarget(false)
            .outerCircleColor(R.color.purple_200)
            .textColor(R.color.white),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    showGuidelinePrompt()
                }
            }
        )
    }

    private fun showGuidelinePrompt(){
        TapTargetView.showFor(this, TapTarget.forView(binding.navView.findViewById(R.id.navigation_guideline), "Guideline","Refer to the guideline of how to take a picture of mangrove plant leaf")
            .tintTarget(false)
            .outerCircleColor(R.color.purple_200)
            .textColor(R.color.white),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    prefEditor = sharedPreferences.edit()
                    prefEditor.putBoolean("didShowPromt", true)
                    prefEditor.apply()
                }
            }
        )
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
    }
}



