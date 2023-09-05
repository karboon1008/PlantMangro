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
import com.example.recycleviewwithclicklistener.Chat.Chat
import com.example.recycleviewwithclicklistener.Chat.LatestMessages
import com.example.recycleviewwithclicklistener.Collection.CollectionPage
import com.example.recycleviewwithclicklistener.Collection.SQLiteHelper
import com.example.recycleviewwithclicklistener.Description.mangroveDescription
import com.example.recycleviewwithclicklistener.Collection.Identify
import com.example.recycleviewwithclicklistener.Guideline.Guideline
import com.example.recycleviewwithclicklistener.Intro.IntroSlider
import com.example.recycleviewwithclicklistener.Intro.IntroSliderAdapter
import com.example.recycleviewwithclicklistener.Login.LoginStatusManager
import com.example.recycleviewwithclicklistener.SignUp.Users
import com.example.recycleviewwithclicklistener.SignUp.Welcome
import com.example.recycleviewwithclicklistener.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var sqLiteHelper: SQLiteHelper
    lateinit var binding: ActivityMainBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var prefEditor: SharedPreferences.Editor
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var mAuth: FirebaseAuth
    private lateinit var loginStatusManager: LoginStatusManager
    private lateinit var mGoogleSignInClient: GoogleSignInClient

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
                R.drawable.identify_slide
            )
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        loginStatusManager = LoginStatusManager(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        displayUsername()

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

                    R.id.navigation_chat -> {
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        val intent = Intent(this@MainActivity, Chat::class.java)
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
                    R.id.logout-> {
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        signOutAndStartWelcomeActivity()
                        loginStatusManager.clearLoginDetails()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        displayUsername()
    }


    private fun displayUsername(){
        val username = findViewById<TextView>(R.id.username)
        val user = mAuth.currentUser
        val userID = user?.uid ?: ""
        val usersRef = FirebaseDatabase.getInstance().getReference("users")

        usersRef.child(userID).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUser = snapshot.getValue(Users::class.java)
                val curentUsername = currentUser?.name ?: "Guest"

                if(user != null){
                    val name = user.displayName
                    if(name != null){
                        username.text = "Welcome,  $name"
                    }else{
                        username.text = "Welcome, $curentUsername"
                    }
                }else{
                    username.text = "You havent sign in yet!"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                username.text = "Something wrong, try again"
            }
        })

    }

    private fun signOutAndStartWelcomeActivity(){
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            val intent = Intent(this@MainActivity, Welcome::class.java)
            startActivity(intent)
            finish()
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



