package com.example.mapnavigationbyaryan_dubey_500109841

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import android.net.Uri
import android.location.Geocoder

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var destinationInput: EditText
    private lateinit var commuteBtn: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private lateinit var currentLatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        mapView = findViewById(R.id.mapView1)
        destinationInput = findViewById(R.id.destinationInput)
        commuteBtn = findViewById(R.id.commuteBtn)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize MapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Set onClickListener for the button
        commuteBtn.setOnClickListener {
            startJourney()
        }
    }

    private fun startJourney() {
        // Get the user's current location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                val destination = destinationInput.text.toString()

                // Navigate using Google Maps
                val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${it.latitude},${it.longitude}&destination=$destination")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)

                // Add markers on the map
                addMarkers(currentLatLng, destination)
            }
        }
    }

    private fun addMarkers(currentLatLng: LatLng, destination: String) {
        googleMap.clear() // Clear existing markers
        googleMap.addMarker(MarkerOptions().position(currentLatLng).title("Your Location"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f)) // Zoom in on the user's location

        // Assume the destination is a valid address that can be geocoded
        // For the demo, we add a marker at the destination directly
        // Ideally, you would need to geocode the destination to get its LatLng
        val destinationLatLng = LatLng(0.0, 0.0) // Replace with geocoding logic to get LatLng from the destination string

        googleMap.addMarker(MarkerOptions().position(destinationLatLng).title("Destination: $destination"))
    }


    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // Enable the My Location layer if permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true

            // Get the user's current location
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLatLng = LatLng(it.latitude, it.longitude)
                    // Move the camera to the user's current location
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
