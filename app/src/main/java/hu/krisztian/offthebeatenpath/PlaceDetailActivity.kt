package hu.krisztian.offthebeatenpath

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import hu.krisztian.offthebeatenpath.model.PlaceResponse
import hu.krisztian.offthebeatenpath.network.RetrofitClient
import hu.krisztian.offthebeatenpath.model.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaceDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.fragment_place_detail)

        // Retrieve the place ID and user ID passed from the adapter
        val placeId = intent.getIntExtra("PLACE_ID", -1)
        val userId = intent.getIntExtra("USER_ID", -1)
        Log.d("PlaceDetailActivity", "Place ID: $placeId, User ID: $userId")
        // Initialize views
        val placeNameTextView: TextView = findViewById(R.id.poiName)
        val categoryTextView: TextView = findViewById(R.id.category)
        val descriptionTextView: TextView = findViewById(R.id.poiDescription)
        val userTextView: TextView = findViewById(R.id.user)

        if (placeId != -1) {
            fetchPlaceDetails(placeId, placeNameTextView, categoryTextView, descriptionTextView)
        } else {
            descriptionTextView.text = "Invalid Place ID"
        }

        if (userId != -1) {
            fetchUserName(userId, userTextView)
        } else {
            userTextView.text = "Unknown User"
        }
    }

    private fun fetchPlaceDetails(
        placeId: Int,
        placeNameTextView: TextView,
        categoryTextView: TextView,
        descriptionTextView: TextView
    ) {
        RetrofitClient.placesService.getPOIById(placeId).enqueue(object : Callback<PlaceResponse> {
            override fun onResponse(call: Call<PlaceResponse>, response: Response<PlaceResponse>) {
                if (response.isSuccessful) {
                    val place = response.body()?.message
                    Log.d("PlaceDetail", "Response Body: ${response.body()}")
                    if (place != null) {
                        placeNameTextView.text = place.poi_name
                        categoryTextView.text = place.category_id.toString()
                        descriptionTextView.text = place.poi_description ?: "No description provided."
                        Log.d("PlaceDetail", "Place Name: ${place.poi_name}, Description: ${place.poi_description}")

                    } else {
                        descriptionTextView.text = "Details not found."
                    }
                } else {
                    descriptionTextView.text = "Failed to fetch details. Try again later."
                }
            }

            override fun onFailure(call: Call<PlaceResponse>, t: Throwable) {
                descriptionTextView.text = "Unable to connect. Check your internet connection."
                Log.e("PlaceDetails", "Error fetching place details: ${t.message}")
            }
        })
    }


    private fun fetchUserName(userId: Int, userTextView: TextView) {
        RetrofitClient.userService.getUserProfile(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { userResponse ->
                        if (userResponse.success) {
                            val username = userResponse.user?.user_name ?: "Unknown User"
                            userTextView.text = getString(R.string.uploaded_by, username)
                        } else {
                            userTextView.text = "User not found."
                        }
                    } ?: run {
                        userTextView.text = "Failed to fetch user details."
                    }
                } else {
                    userTextView.text = "Failed to fetch user details."
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                userTextView.text = "Unable to connect to fetch user details."
            }
        })
    }



}
