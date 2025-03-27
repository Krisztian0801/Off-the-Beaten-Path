package hu.krisztian.offthebeatenpath

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import hu.krisztian.offthebeatenpath.model.Category
import hu.krisztian.offthebeatenpath.model.Landmark
import hu.krisztian.offthebeatenpath.model.PlaceRequest
import hu.krisztian.offthebeatenpath.model.PlaceResponse
import hu.krisztian.offthebeatenpath.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPlaceBottomSheetFragment(
    private val lat: Double,
    private val lng: Double,
    private val userId: Int, // The ID of the current user
    private val onPlaceAdded: (PlaceResponse) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.add_place, container, false)

        val etPlaceName = view.findViewById<TextInputEditText>(R.id.placeNameTextView)
        val categoryDropdown = view.findViewById<AutoCompleteTextView>(R.id.categoryDropdown)
        val landmarkDropdown = view.findViewById<AutoCompleteTextView>(R.id.landmarkDropdown)
        val btnSave = view.findViewById<Button>(R.id.savePlaceButton)

        btnSave.text = getString(R.string.add_place) // Using string resources for better maintainability

        populateCategories(categoryDropdown)
        populateLandmarks(landmarkDropdown)

        btnSave.setOnClickListener {
            val placeName = etPlaceName.text.toString().trim()
            val categoryName = categoryDropdown.text.toString().trim()
            val landmarkName = landmarkDropdown.text.toString().trim()

            if (placeName.isEmpty()) {
                etPlaceName.error = getString(R.string.error_place_name_empty)
                return@setOnClickListener
            }

            if (categoryName.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.error_select_category), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (landmarkName.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.error_select_landmark), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                val categoryId = getCategoryId(categoryName)
                val landmarkId = getLandmarkId(landmarkName)

                if (categoryId == 0 || landmarkId == 0) {
                    Toast.makeText(requireContext(), getString(R.string.error_invalid_selection), Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val placeRequest = PlaceRequest(
                    poi_name = placeName,
                    poi_discription = "$categoryName - $landmarkName",
                    latitude = lat,
                    longitude = lng,
                    landmark_id = landmarkId,
                    category_id = categoryId,
                    user_id = userId
                )

                savePlaceToAPI(placeRequest)
            }
        }

        return view
    }

    private fun populateCategories(categoryDropdown: AutoCompleteTextView) {
        RetrofitClient.categoryService.getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    response.body()?.let { categories ->
                        val categoryNames = categories.map { it.category_name }
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            categoryNames
                        )
                        categoryDropdown.setAdapter(adapter)
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.error_fetch_categories), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(requireContext(), "${getString(R.string.error_fetch_categories)}: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateLandmarks(landmarkDropdown: AutoCompleteTextView) {
        RetrofitClient.landmarkService.getLandmarks().enqueue(object : Callback<List<Landmark>> {
            override fun onResponse(call: Call<List<Landmark>>, response: Response<List<Landmark>>) {
                if (response.isSuccessful) {
                    response.body()?.let { landmarks ->
                        val landmarkNames = landmarks.map { it.landmark_discription }
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            landmarkNames
                        )
                        landmarkDropdown.setAdapter(adapter)
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.error_fetch_landmarks), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Landmark>>, t: Throwable) {
                Toast.makeText(requireContext(), "${getString(R.string.error_fetch_landmarks)}: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private suspend fun getCategoryId(categoryName: String): Int {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.categoryService.getCategories().execute()
                if (response.isSuccessful) {
                    response.body()?.find { it.category_name == categoryName }?.category_id?.toIntOrNull() ?: 0
                } else {
                    0
                }
            } catch (e: Exception) {
                0
            }
        }
    }

    private suspend fun getLandmarkId(landmarkName: String): Int {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.landmarkService.getLandmarks().execute()
                if (response.isSuccessful) {
                    response.body()?.find { it.landmark_discription == landmarkName }?.landmark_id?.toIntOrNull() ?: 0
                } else {
                    0
                }
            } catch (e: Exception) {
                0
            }
        }
    }

    private fun savePlaceToAPI(placeRequest: PlaceRequest) {
        RetrofitClient.placesService.addPOI(placeRequest).enqueue(object : Callback<PlaceResponse> {
            override fun onResponse(call: Call<PlaceResponse>, response: Response<PlaceResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.let { newPlace ->
                        onPlaceAdded(newPlace)
                        dismiss()
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.error_save_place), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PlaceResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "${getString(R.string.error_save_place)}: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
