package com.example.recycleviewwithclicklistener

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.recycleviewwithclicklistener.Collection.SQLiteHelper
import com.google.android.gms.maps.model.MarkerOptions
import com.example.recycleviewwithclicklistener.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.ui.IconGenerator

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sqLiteHelper: SQLiteHelper

    companion object{
        private const val LOCATION_REQUEST_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Maps"

        sqLiteHelper = SQLiteHelper(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val iconGenerator = IconGenerator(this)
        iconGenerator.setTextAppearance(com.google.maps.android.R.style.amu_Bubble_TextAppearance_Light)
        iconGenerator.setBackground(ContextCompat.getDrawable(this, R.drawable.nav_head_icon))


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)

        val (namelist, mglolist, imagelist) = sqLiteHelper.getLocationMangrove()
        for (i in mglolist.indices) {
            for (i in namelist.indices) {
                for (i in imagelist.indices) {
                    val name = namelist[i]
                    val location = mglolist[i]
                    val markerOptions = MarkerOptions()
                        .position(location)
                        .title(name)

                    // set marker color based on species name
                    when (name) {
                        "Avicennia marina" -> markerOptions.icon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_RED
                            )
                        )
                        "Avicennia officinalis" -> markerOptions.icon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_BLUE
                            )
                        )
                        "Bruguiera sexangula" -> markerOptions.icon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_ORANGE
                            )
                        )
                        "Rhizophora apiculata" -> markerOptions.icon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN
                            )
                        )
                        "Sonneratia caseolaris" -> markerOptions.icon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_VIOLET
                            )
                        )
                    }
                    mMap.addMarker(markerOptions)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mglolist[i], 18f))
                }
            }
        }
        setUpMap()
        mMap.setOnMarkerClickListener { marker->
            val title = marker.title
            val location = marker.position
            val dialogBuilder = AlertDialog.Builder(this)

            // Create a layout inflater to inflate the dialog layout
            val inflater = LayoutInflater.from(this@MapsActivity)
            val dialogLayout = inflater.inflate(R.layout.popout_maps, null)
            val image_popout_maps = dialogLayout.findViewById<ImageView>(R.id.image_popout_maps)
            val title_popout_maps = dialogLayout.findViewById<TextView>(R.id.title_popout_maps)

            // Find the index of the clicked marker in the namelist
            val index = sqLiteHelper.getLocationMangrove().second.indexOf(location)

            /* Get the image from the imagelist using the same index */
            val image = sqLiteHelper.getLocationMangrove().third[index]

            val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)

            image_popout_maps.setImageBitmap(bitmap)
            title_popout_maps.text = title

            dialogBuilder.setView(dialogLayout)
            dialogBuilder.setPositiveButton("OK", null)
            dialogBuilder.show()
            true
        }

    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true

        // for collection details (click on location then go to the specific location on maps)
        val intent = intent
        val location = intent.getStringExtra("latlng")
        val lat_list = location?.split(",")
        val lat = (lat_list?.get(0))?.toDouble()
        val lng = (lat_list?.get(1))?.toDouble()
        val latlng = lat?.let { lng?.let { it1 -> LatLng(it, it1) } }

        if(latlng!=null){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 25f))
        }
    }
    override fun onMarkerClick(p0: Marker) = false

}