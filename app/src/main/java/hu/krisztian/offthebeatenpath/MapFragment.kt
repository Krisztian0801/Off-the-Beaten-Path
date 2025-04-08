package hu.krisztian.offthebeatenpath

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hu.krisztian.offthebeatenpath.model.CoordinateResponse
import hu.krisztian.offthebeatenpath.model.Place
import hu.krisztian.offthebeatenpath.model.PlacesListResponse
import hu.krisztian.offthebeatenpath.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        setupMapFragment()
        setupMyLocationButton(view)
        return view
    }

    private fun setupMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupMyLocationButton(view: View) {
        val myLocationButton: FloatingActionButton = view.findViewById(R.id.my_location_button)
        myLocationButton.setOnClickListener {
            moveToCurrentLocation()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (checkLocationPermissions()) {
            enableMyLocation()
            getLastKnownLocation()
        }

        loadPlacesFromAPI() // Fetch places from API

        googleMap.setOnMapLongClickListener { latLng ->
            showAddPlaceBottomSheet(latLng)
        }
    }
    private fun showAddPlaceBottomSheet(latLng: LatLng) {
        val userIdString = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE).getString("user_id", null)
        val userId = userIdString?.toIntOrNull() ?: -1
        val bottomSheet = AddPlaceBottomSheetFragment(
            latLng.latitude,
            latLng.longitude,
            userId =  userId // Replace with actual user ID"
        ) { newPlace ->
            fetchCoordinatesAndAddMarker(newPlace.message)
        }
        bottomSheet.show(parentFragmentManager, "AddPlaceBottomSheet")
    }

    private fun addMarker(latLng: LatLng, title: String, description: String) {
        googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(description)
        )
    }
    private fun loadPlacesFromAPI() {
        val apiService = RetrofitClient.placesService
        val call = apiService.getAllPOIs()

        call.enqueue(object : Callback<PlacesListResponse> {
            override fun onResponse(call: Call<PlacesListResponse>, response: Response<PlacesListResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { placesList ->
                        for (place in placesList.message) {
                            fetchCoordinatesAndAddMarker(place)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<PlacesListResponse>, t: Throwable) {
                Toast.makeText(requireContext(), getString(R.string.failed_to_load_places, t.message), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchCoordinatesAndAddMarker(place: Place) {
        val apiService = RetrofitClient.placesService
        val call = apiService.getCoordinates(place.coordinate_id)

        call.enqueue(object : Callback<CoordinateResponse> {
            override fun onResponse(call: Call<CoordinateResponse>, response: Response<CoordinateResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { coordinateResponse ->
                        place.latitude = coordinateResponse.message.coordinate_latitude
                        place.longitude = coordinateResponse.message.coordinate_longitude
                        addMarker(LatLng(place.latitude!!, place.longitude!!), place.poi_name, place.poi_discription ?: "")
                    }
                }
            }

            override fun onFailure(call: Call<CoordinateResponse>, t: Throwable) {
                Toast.makeText(requireContext(), getString(R.string.failed_to_fetch_coordinates, t.message), Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun checkLocationPermissions(): Boolean {
        return if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions()
            false
        } else {
            true
        }
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
        }
        googleMap.uiSettings.isMyLocationButtonEnabled = false
    }

    private fun getLastKnownLocation() {
        if (checkLocationPermissions() && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                } else {
                    val defaultLatLng = LatLng(47.497913, 19.040236) // Default location (Budapest)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 10f))
                }
            }
        }
    }

    private fun moveToCurrentLocation() {
        val toastLocation = resources.getString(R.string.toastLocation)
        currentLocation?.let {
            val currentLatLng = LatLng(it.latitude, it.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
        } ?: run {
            Toast.makeText(requireContext(), "$toastLocation!", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastKnownLocation()
            } else {
                Toast.makeText(requireContext(), R.string.location_permission_denied, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        @JvmStatic
        fun newInstance() = MapFragment()
    }
}
