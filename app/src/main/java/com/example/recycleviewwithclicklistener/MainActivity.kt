package com.example.recycleviewwithclicklistener

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var search:SearchView
    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var mangrovelist: ArrayList<Mangrove>
    private lateinit var mangroveAdapter:MangroveAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sqLiteHelper = SQLiteHelper(this)
        search = findViewById(R.id.idSV)

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

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    recyclerView.visibility = View.VISIBLE
                    galleryButton.visibility = View.VISIBLE
                    cameraButton.visibility = View.VISIBLE
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
        init()
        // search
        // Declare filteredMangrovelist as a global variable
        var filteredMangrovelist: MutableList<Mangrove> = mutableListOf()

        // add data into the list
        filteredMangrovelist.addAll(mangrovelist)

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search.clearFocus()
                if (query != null) {
                    // Filter the mangrovelist based on the query
                    val filteredList = filteredMangrovelist.filter { it.name.contains(query, true) }.toMutableList()

                    if (filteredList.isNotEmpty()) {
                        mangroveAdapter.filter.filter(query)
                        mangroveAdapter.updateList(filteredList)
                    } else {
                        Toast.makeText(this@MainActivity, "No Species found..", Toast.LENGTH_LONG).show()
                    }
                }
                else {
                    Toast.makeText(this@MainActivity, "Back to original..", Toast.LENGTH_LONG).show()
                    // If the query is empty, reset the RecyclerView to show the original list
                    mangroveAdapter = MangroveAdapter(mangrovelist)
                    recyclerView.adapter = mangroveAdapter

                }
                return false

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the filteredmangrovelist based on the query
                mangroveAdapter.updateList(filteredMangrovelist)
                return false
            }
        })
    }

    private fun getMangrove(){
        val mgList = sqLiteHelper.getAllMangrove()
        Log.e("pppp","${mgList.size}")
    }

    private fun init() {
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        mangrovelist = ArrayList()
        addDataToList()
        mangroveAdapter = MangroveAdapter(mangrovelist)
        recyclerView.adapter = mangroveAdapter

        mangroveAdapter.onItemClick = {
            val intent = Intent(this, DetailedActivity::class.java)
            intent.putExtra("mangrove", it)
            startActivity(intent)
        }
    }

    private fun addDataToList(){
        mangrovelist.add(Mangrove(R.drawable.amarina, "Avicennia marina", "Api-api Jambu","Also known as Api-api Jambu, is a mangrove tree. Unlike other species, the young branches of Api-api Jambu is distinctly square shaped. The fruit is greyish green with a short beak at the tip.","Salt-tolerant mangrove with multiple ecological benefits."))
        mangrovelist.add(Mangrove(R.drawable.aofficinalis, "Avicennia officinalis", "Api Api Ludat", "Also known as Api Api Ludat, is a mangrove tree. It has large orange-yellow flowers that smell rancid. The leaves are oblong shaped and the underside are distinctly yellowish green.", "Medicinal, salt-tolerant mangrove species."))
        mangrovelist.add(Mangrove(R.drawable.bsexangula, "Bruguiera sexangula","Tumu Mata Buaya" , "A mangrove tree species that is commonly found in the coastal regions of Southeast Asia, including India, Bangladesh, and Sri Lanka. It is a salt-tolerant plant that grows in mudflats and tidal creeks and provides important habitats for various species of wildlife, including fish, crustaceans, and birds. B. sexangula is also known for its strong, durable wood and is used for construction, fuel, and other purposes.", "Dense, saltwater-tolerant mangrove species."))
        mangrovelist.add(Mangrove(R.drawable.rapiculata, "Rhizophora apiculata","Bakau Minyak" ,"Also known as the mangrove red apple, is a species of mangrove tree native to Southeast Asia. It is commonly found in tidal mudflats and intertidal zones, and is known for its remarkable ability to survive in harsh coastal environments. The tree produces a red fruit that is important to wildlife, and provides essential habitat and protection for many species of marine life.", "Mangrove tree with red aerial roots."))
        mangrovelist.add(Mangrove(R.drawable.scaseolaris, "Sonneratia caseolaris", "Berembang", "A mangrove plant species with aerial prop roots, leathery leaves, and fragrant white flowers that bloom at night. It has salt-tolerant adaptations and is used in traditional medicine and as a source of food and fuel in coastal areas.", "Salt-tolerant mangrove plant with edible fruit"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data

            // Load the image from the Uri into a Bitmap object
            val imageStream = selectedImageUri?.let { contentResolver.openInputStream(it) }

            val intent = Intent(this, result_activity::class.java)
            intent.putExtra("image", selectedImageUri.toString())
            startActivity(intent)


        }
    }
}



