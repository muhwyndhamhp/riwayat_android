package io.muhwyndhamhp.riwayat.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.LOCATION_ADDRESS
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.LOCATION_LAT
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.LOCATION_LONG
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.LOCATION_NAME
import kotlinx.android.synthetic.main.activity_location_picker.*
import java.util.*


class LocationPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        @JvmStatic
        private val DEFAULT_ZOOM = 15

        @JvmStatic
        private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        @JvmStatic
        private val M_MAX_ENTRIES = 5
    }

    // New variables for Current Place Picker
    private val TAG = "MapsActivity"
    var lstPlaces: ListView? = null
    private var mPlacesClient: PlacesClient? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var mLastKnownLocation: Location? = null

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private val mDefaultLocation =
        LatLng(-33.8523341, 151.2106085)

    private var mLocationPermissionGranted = false

    // Used for selecting the current place.

    private var mLikelyPlaceNames: Array<String?> = arrayOf()
    private var mLikelyPlaceAddresses: Array<String?> = arrayOf()
    private var mLikelyPlaceAttributions: Array<String?> = arrayOf()
    private var mLikelyPlaceLatLngs: Array<LatLng?> = arrayOf()

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        setSupportActionBar(toolbar)

        lstPlaces = findViewById(R.id.listPlaces)

        val apiKey = getString(R.string.google_maps_key)
        Places.initialize(applicationContext, apiKey)
        mPlacesClient = Places.createClient(this)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_geolocate -> {
                pickCurrentPlace()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.uiSettings.isZoomControlsEnabled = true

        getLocationPermission()

        mMap.setOnMapClickListener {

            val geocoder = Geocoder(this, Locale.getDefault())
            val address = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            mMap.addMarker(
                MarkerOptions()
                    .title(if(address[0].featureName != null) address[0].featureName else it.toString())
                    .position(it)
                    .snippet(address[0].getAddressLine(0))
            )
        }

        mMap.setOnInfoWindowClickListener {
//            val toast = Toast.makeText(this, it.title + " " + it.snippet, Toast.LENGTH_SHORT)
//            toast.show()

            val intent = Intent()
            intent.putExtra(LOCATION_NAME, it.title)
            intent.putExtra(LOCATION_LAT, it.position.latitude.toString())
            intent.putExtra(LOCATION_LONG, it.position.longitude.toString())
            intent.putExtra(LOCATION_ADDRESS, it.snippet)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun getCurrentPlaceLikelihoods() {
        // Use fields to define the data types to return.
        val placeFields: List<Place.Field> = listOf(
            Place.Field.NAME, Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        // Get the likely places - that is, the businesses and other points of interest that
        // are the best match for the device's current location.
        val request = FindCurrentPlaceRequest.builder(placeFields).build()
        val placeResponse =
            mPlacesClient!!.findCurrentPlace(request)
        placeResponse.addOnCompleteListener(
            this
        ) { task ->
            if (task.isSuccessful) {
                val response = task.result
                // Set the count, handling cases where less than 5 entries are returned.
                val count: Int
                count = if (response!!.placeLikelihoods.size < M_MAX_ENTRIES) {
                    response.placeLikelihoods.size
                } else {
                    M_MAX_ENTRIES
                }
                var i = 0
                mLikelyPlaceNames = arrayOfNulls(count)
                mLikelyPlaceAddresses = arrayOfNulls(count)
                mLikelyPlaceAttributions = arrayOfNulls(count)
                mLikelyPlaceLatLngs =
                    arrayOfNulls(count)
                for (placeLikelihood in response.placeLikelihoods) {
                    val currPlace = placeLikelihood.place
                    mLikelyPlaceNames[i] = currPlace.name!!
                    mLikelyPlaceAddresses[i] = currPlace.address!!
                    mLikelyPlaceAttributions[i] =
                        (if (currPlace.attributions == null) null else TextUtils.join(
                            " ",
                            currPlace.attributions!!
                        ))
                    mLikelyPlaceLatLngs[i] = currPlace.latLng!!
                    val currLatLng =
                        if (mLikelyPlaceLatLngs[i] == null) "" else mLikelyPlaceLatLngs[i].toString()
                    Log.i(
                        TAG, String.format(
                            "Place " + currPlace.name
                                    + " has likelihood: " + placeLikelihood.likelihood
                                    + " at " + currLatLng
                        )
                    )
                    i++
                    if (i > count - 1) {
                        break
                    }
                }


                // COMMENTED OUT UNTIL WE DEFINE THE METHOD
                // Populate the ListView
                fillPlacesList()

                for (j in mLikelyPlaceAddresses.indices) {
                    addMarkers(j)
                }
            } else {
                val exception = task.exception
                if (exception is ApiException) {
                    Log.e(TAG, "Place not found: " + exception.statusCode)
                }
            }
        }
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                val locationResult: Task<Location> =
                    mFusedLocationProviderClient!!.lastLocation
                locationResult.addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult()
                        Log.d(
                            TAG,
                            "Latitude: " + mLastKnownLocation!!.latitude
                        )
                        Log.d(
                            TAG,
                            "Longitude: " + mLastKnownLocation!!.longitude
                        )
                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    mLastKnownLocation!!.latitude,
                                    mLastKnownLocation!!.longitude
                                ), DEFAULT_ZOOM.toFloat()
                            )
                        )

                        val geocoder = Geocoder(this, Locale.getDefault())
                        val address = geocoder.getFromLocation(mLastKnownLocation!!.latitude, mLastKnownLocation!!.longitude, 1)

                        mMap.addMarker(
                            MarkerOptions()
                                .title("Your Location")
                                .position(
                                    LatLng(
                                        mLastKnownLocation!!.latitude,
                                        mLastKnownLocation!!.longitude
                                    )
                                )
                                .snippet(address[0].getAddressLine(0))
                        )

                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        mMap.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                    }
                    getCurrentPlaceLikelihoods()
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message!!)
        }
    }

    private fun pickCurrentPlace() {
        if (mLocationPermissionGranted) {
            getDeviceLocation()
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.")

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(
                MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet))
            )

            // Prompt the user for permission.
            getLocationPermission()
        }
    }

    private fun fillPlacesList() {
        // Set up an ArrayAdapter to convert likely places into TextViews to populate the ListView
        val placesAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            mLikelyPlaceNames
        )!!
        lstPlaces!!.adapter = placesAdapter
        lstPlaces!!.onItemClickListener = listClickedHandler
    }

    private val listClickedHandler: AdapterView.OnItemClickListener =
        AdapterView.OnItemClickListener { _, _, position, _ -> // position will give us the index of which place was selected in the array
            val markerLatLng =
                mLikelyPlaceLatLngs[position]!!
            var markerSnippet = mLikelyPlaceAddresses[position]!!
            if (mLikelyPlaceAttributions[position] != null) {
                markerSnippet = """
                        $markerSnippet
                        ${mLikelyPlaceAttributions[position]}
                        """.trimIndent()
            }

            // Add a marker for the selected place, with an info window
            // showing information about that place.
            mMap.addMarker(
                MarkerOptions()
                    .title(mLikelyPlaceNames[position])
                    .position(markerLatLng)
                    .snippet(markerSnippet)
            )

            // Position the map's camera at the location of the marker.
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLatLng))
        }

    private fun addMarkers(position: Int) {
        val markerLatLng =
            mLikelyPlaceLatLngs[position]!!
        var markerSnippet = mLikelyPlaceAddresses[position]!!
        if (mLikelyPlaceAttributions[position] != null) {
            markerSnippet = """
                        $markerSnippet
                        ${mLikelyPlaceAttributions[position]}
                        """.trimIndent()
        }

        // Add a marker for the selected place, with an info window
        // showing information about that place.
        mMap.addMarker(
            MarkerOptions()
                .title(mLikelyPlaceNames[position])
                .position(markerLatLng)
                .snippet(markerSnippet)
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    mLocationPermissionGranted = true
                }
            }
        }
    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        mLocationPermissionGranted = false
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }
}
