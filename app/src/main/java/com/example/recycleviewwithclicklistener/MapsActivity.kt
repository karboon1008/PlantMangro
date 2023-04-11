package com.example.recycleviewwithclicklistener

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.MarkerOptions
import com.example.recycleviewwithclicklistener.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.ui.IconGenerator
import kotlin.system.measureNanoTime

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
        supportActionBar!!.title = "Location"

        sqLiteHelper = SQLiteHelper(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val iconGenerator = IconGenerator(this)
        iconGenerator.setTextAppearance(com.google.maps.android.R.style.amu_Bubble_TextAppearance_Light)
        iconGenerator.setBackground(ContextCompat.getDrawable(this, R.drawable.mangroveicon))


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)

        val (namelist, mglolist) = sqLiteHelper.getLocationMangrove()
        for (i in mglolist!!.indices) {
            for (i in namelist!!.indices) {
                val name = namelist!![i]
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
        setUpMap()

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
    }
    override fun onMarkerClick(p0: Marker) = false

}