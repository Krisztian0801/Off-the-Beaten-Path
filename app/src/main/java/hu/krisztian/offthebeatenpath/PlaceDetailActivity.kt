package hu.krisztian.offthebeatenpath

import android.app.AlertDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import hu.krisztian.offthebeatenpath.model.CategoryResponse
import hu.krisztian.offthebeatenpath.model.CoordinateResponse
import hu.krisztian.offthebeatenpath.model.Place
import hu.krisztian.offthebeatenpath.model.PlaceRequest
import hu.krisztian.offthebeatenpath.model.PlaceResponse
import hu.krisztian.offthebeatenpath.model.PlaceUpdateResponse
import hu.krisztian.offthebeatenpath.network.RetrofitClient
import hu.krisztian.offthebeatenpath.model.UserResponse
import hu.krisztian.offthebeatenpath.network.DropdownHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaceDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var editPlaceNameEditText: TextInputEditText
    private lateinit var editCategoryDropdown: AutoCompleteTextView
    private lateinit var editLandmarkDropdown: AutoCompleteTextView
    private lateinit var saveEditPlaceButton: Button
    private lateinit var placeDetails: LinearLayout
    private lateinit var editPlaceDetails: LinearLayout

    private var landmarkName: String? = null
    private var placeName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.fragment_place_detail)


        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        editPlaceNameEditText = findViewById(R.id.editPlaceNameEditText)
        editCategoryDropdown = findViewById(R.id.editCategoryDropdown)
        editLandmarkDropdown = findViewById(R.id.editLandmarkDropdown)
        saveEditPlaceButton = findViewById(R.id.saveEditPlaceButton)
        placeDetails = findViewById(R.id.placeDetails)
        editPlaceDetails = findViewById(R.id.editPlaceDetails)

        val placeNameTextView: TextView = findViewById(R.id.poiName)
        val categoryTextView: TextView = findViewById(R.id.category)
        val descriptionTextView: TextView = findViewById(R.id.poiDescription)
        val userTextView: TextView = findViewById(R.id.user)

        toolbar.setNavigationOnClickListener {
            finish()
        }
        DropdownHelper.populateCategories(this, editCategoryDropdown)
        DropdownHelper.populateLandmarks(this, editLandmarkDropdown)

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    placeDetails.visibility = View.GONE
                    editPlaceDetails.visibility = View.VISIBLE

                    editPlaceNameEditText.setText(placeNameTextView.text.toString())
                    editCategoryDropdown.setText(categoryTextView.text.toString(), false)
                    editLandmarkDropdown.setText(landmarkName, false)
                    true
                }

                R.id.action_delete -> {
                    confirmDeletePlace()
                    true
                }

                else -> false
            }
        }


        saveEditPlaceButton.setOnClickListener {
            saveEditedPlace()
        }
        val placeId = intent.getIntExtra("PLACE_ID", -1)
        val userId = intent.getIntExtra("USER_ID", -1)
        val categoryId = intent.getIntExtra("CATEGORY_ID", -1)
        Log.d("PlaceDetailActivity", "Place ID: $placeId, User ID: $userId")

        // Initialize views
        CoroutineScope(Dispatchers.Main).launch {
            if (placeId != -1) {
                fetchPlaceDetails(placeId, placeNameTextView, categoryTextView, descriptionTextView)
            } else {
                descriptionTextView.text = getString(R.string.place_not_found)
            }
        }

        if (userId != -1) {
            fetchUserName(userId, userTextView)
        } else {
            userTextView.text = getString(R.string.unknown_user)
        }
        if (categoryId != -1) {
            fetchCategoryName(categoryId, categoryTextView)
        } else {
            categoryTextView.text = getString(R.string.unknown_category)
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.place_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun saveEditedPlace() {
        val placeName = editPlaceNameEditText.text.toString().trim()
        val categoryName = editCategoryDropdown.text.toString().trim()
        val landmarkName = editLandmarkDropdown.text.toString().trim()

        if (placeName.isEmpty()) {
            editPlaceNameEditText.error = getString(R.string.error_place_name_empty)
            return
        }

        if (categoryName.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_select_category), Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (landmarkName.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_select_landmark), Toast.LENGTH_SHORT)
                .show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val categoryId = DropdownHelper.getCategoryId(categoryName)
            val landmarkId = DropdownHelper.getLandmarkId(landmarkName)

            if (categoryId == 0 || landmarkId == 0) {
                Toast.makeText(
                    this@PlaceDetailActivity,
                    getString(R.string.error_invalid_selection),
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            val userIdString =
                getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("user_id", null)
            val userId = userIdString?.toIntOrNull() ?: -1
            val placeId = intent.getIntExtra("PLACE_ID", -1)

            val placeRequest = PlaceRequest(
                poi_name = placeName,
                poi_discription = "$categoryName - $landmarkName",
                latitude = null,
                longitude = null,
                landmark_id = landmarkId,
                category_id = categoryId,
                user_id = userId
            )

            RetrofitClient.placesService.updatePOI(placeId, placeRequest)
                .enqueue(object : Callback<PlaceUpdateResponse> {
                    override fun onResponse(
                        call: Call<PlaceUpdateResponse>,
                        response: Response<PlaceUpdateResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(
                                this@PlaceDetailActivity,
                                getString(R.string.success_save_place),
                                Toast.LENGTH_SHORT
                            ).show()
                            hideKeyboard()
                        } else {
                            Toast.makeText(
                                this@PlaceDetailActivity,
                                getString(R.string.error_save_place),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<PlaceUpdateResponse>, t: Throwable) {
                        Toast.makeText(
                            this@PlaceDetailActivity,
                            "${getString(R.string.error_save_place)}: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("HIBA", "${getString(R.string.error_save_place)}: ${t.message}\"")
                    }
                })

        }
    }

    private fun deletePlace() {
        val placeId = intent.getIntExtra("PLACE_ID", -1)

        RetrofitClient.placesService.deletePOI(placeId)
            .enqueue(object : Callback<PlaceUpdateResponse> {
                override fun onResponse(
                    call: Call<PlaceUpdateResponse>,
                    response: Response<PlaceUpdateResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@PlaceDetailActivity,
                            getString(R.string.place_deleted_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@PlaceDetailActivity,
                            getString(R.string.cannot_deleted_the_place),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PlaceUpdateResponse>, t: Throwable) {
                    Toast.makeText(
                        this@PlaceDetailActivity,
                        getString(R.string.cannot_deleted_the_place),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("HIBA", "${getString(R.string.cannot_deleted_the_place)} : ${t.message}")
                }
            })
    }

    private fun confirmDeletePlace() {
        AlertDialog.Builder(this@PlaceDetailActivity)
            .setTitle(getString(R.string.delete_place))
            .setMessage(
                getString(
                    R.string.are_you_sure_you_want_to_delete_named_place_this_action_cannot_be_undone,
                    placeName
                )
            )
            .setPositiveButton(getString(R.string.yes_delete)) { _, _ ->
                deletePlace()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val loggedInUserId = getLoggedInUserId()
        val isAdmin = checkIfUserIsAdmin()
        val targetUserId = intent.getIntExtra("USER_ID", -1)

        if (loggedInUserId == targetUserId || isAdmin) {
            menuInflater.inflate(R.menu.menu_profile, menu)
            return true
        }

        return false
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val loggedInUserId = getLoggedInUserId()
        val isAdmin = checkIfUserIsAdmin()
        val targetUserId = intent.getIntExtra("USER_ID", -1)

        val editMenuItem = menu?.findItem(R.id.action_edit)
        val deleteMenuItem = menu?.findItem(R.id.action_delete)


        if (loggedInUserId == targetUserId || isAdmin) {
            editMenuItem?.setShowAsAction(1)
            deleteMenuItem?.setShowAsAction(1)
        } else {
            Log.d(
                "MenuVisibility",
                "User ID: $loggedInUserId, Target User ID: $targetUserId, Admin: $isAdmin"
            )
            editMenuItem?.setShowAsAction(0)
            deleteMenuItem?.setShowAsAction(0)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun getLoggedInUserId(): Int {
        val userIdString =
            getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("user_id", null)
        val userId = userIdString?.toIntOrNull() ?: -1
        return userId
    }

    private fun checkIfUserIsAdmin(): Boolean {
        val isAdmin = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getInt("isAdmin", 0)
        return (isAdmin == 1)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val placeId = intent.getIntExtra("PLACE_ID", -1)


        loadPlaceFromAPI(placeId)
    }

    private fun addMarker(latLng: LatLng, title: String, description: String) {
        googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(description)
        )
    }

    private fun loadPlaceFromAPI(placeId: Int) {
        val apiService = RetrofitClient.placesService
        val call = apiService.getPOIById(placeId)

        call.enqueue(object : Callback<PlaceResponse> {
            override fun onResponse(call: Call<PlaceResponse>, response: Response<PlaceResponse>) {
                if (response.isSuccessful) {
                    val place = response.body()?.message
                    Log.d("PlaceDetail", "Response Body: ${response.body()}")
                    if (place != null) {
                        placeName = place.poi_name
                        fetchCoordinatesAndAddMarker(place)
                    } else {
                        Toast.makeText(
                            this@PlaceDetailActivity,
                            getString(R.string.place_not_found), Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@PlaceDetailActivity,
                        getString(R.string.failed_to_load_place, response.code().toString()),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<PlaceResponse>, t: Throwable) {
                Toast.makeText(
                    this@PlaceDetailActivity,
                    getString(R.string.failed_to_load_places, t.message), Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun fetchCoordinatesAndAddMarker(place: Place) {
        val apiService = RetrofitClient.placesService
        val call = apiService.getCoordinates(place.coordinate_id)

        call.enqueue(object : Callback<CoordinateResponse> {
            override fun onResponse(
                call: Call<CoordinateResponse>,
                response: Response<CoordinateResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { coordinateResponse ->
                        place.latitude = coordinateResponse.message.coordinate_latitude
                        place.longitude = coordinateResponse.message.coordinate_longitude

                        addMarker(
                            LatLng(place.latitude!!, place.longitude!!),
                            place.poi_name,
                            place.poi_discription ?: ""
                        )

                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    place.latitude!!,
                                    place.longitude!!
                                ), 15f
                            )
                        )
                    }
                } else {
                    Toast.makeText(
                        this@PlaceDetailActivity,
                        getString(R.string.failed_to_fetch_coordinates, ""), Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<CoordinateResponse>, t: Throwable) {
                Toast.makeText(
                    this@PlaceDetailActivity,
                    getString(R.string.error_fetching_coordinates, t.message), Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private suspend fun fetchPlaceDetails(
        placeId: Int,
        placeNameTextView: TextView,
        categoryTextView: TextView,
        descriptionTextView: TextView
    ) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.placesService.getPOIById(placeId).execute()
                if (response.isSuccessful) {
                    val place = response.body()?.message
                    if (place != null) {
                        withContext(Dispatchers.Main) {
                            placeNameTextView.text = place.poi_name
                            descriptionTextView.text =
                                place.poi_discription ?: getString(R.string.no_description_provided)
                        }

                        fetchLandmarkName(place.landmark_id)

                    } else {
                        withContext(Dispatchers.Main) {
                            descriptionTextView.text = getString(R.string.details_not_found)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        descriptionTextView.text =
                            getString(R.string.failed_to_fetch_details_try_again_later)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    descriptionTextView.text =
                        getString(R.string.unable_to_connect_check_your_internet_connection)
                }
            }
        }
    }


    private fun fetchUserName(userId: Int, userTextView: TextView) {
        RetrofitClient.userService.getUserProfile(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { userResponse ->
                        if (userResponse.success) {
                            val username =
                                userResponse.user?.user_name ?: getString(R.string.unknown_user)
                            userTextView.text = getString(R.string.uploaded_by, username)
                        } else {
                            userTextView.text = getString(R.string.user_not_found)
                        }
                    } ?: run {
                        userTextView.text = getString(R.string.failed_to_fetch_user_details)
                    }
                } else {
                    userTextView.text = getString(R.string.failed_to_fetch_user_details)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                userTextView.text = getString(R.string.unable_to_connect_to_fetch_user_details)
            }
        })
    }

    private fun fetchCategoryName(categoryId: Int, categoryTextView: TextView) {
        RetrofitClient.categoryService.getCategory(categoryId)
            .enqueue(object : Callback<CategoryResponse> {
                override fun onResponse(
                    call: Call<CategoryResponse>,
                    response: Response<CategoryResponse>
                ) {
                    categoryTextView.post {
                        if (response.isSuccessful) {
                            response.body()?.let { categoryResponse ->
                                categoryTextView.text = categoryResponse.category
                            } ?: run {
                                categoryTextView.text = getString(R.string.category_not_found)
                            }
                        } else {
                            categoryTextView.text =
                                getString(R.string.error, response.code().toString())
                        }
                    }
                }

                override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                    categoryTextView.post {
                        categoryTextView.text =
                            getString(R.string.network_error, t.localizedMessage)
                    }
                }
            })
    }

    private suspend fun fetchLandmarkName(landmarkId: Int) {
        withContext(Dispatchers.IO) {
            try {
                val landmark = RetrofitClient.landmarkService.getLandmark(landmarkId)
                landmarkName = landmark.landmark_discription
            } catch (e: Exception) {
                landmarkName = null
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editPlaceNameEditText.windowToken, 0)
    }
}



