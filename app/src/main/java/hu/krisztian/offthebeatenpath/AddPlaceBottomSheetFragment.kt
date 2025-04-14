package hu.krisztian.offthebeatenpath

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import hu.krisztian.offthebeatenpath.model.PlaceRequest
import hu.krisztian.offthebeatenpath.model.PlaceResponse
import hu.krisztian.offthebeatenpath.network.DropdownHelper
import hu.krisztian.offthebeatenpath.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

        val etPlaceName = view.findViewById<TextInputEditText>(R.id.laceNameEditText)
        val categoryDropdown = view.findViewById<AutoCompleteTextView>(R.id.categoryDropdown)
        val landmarkDropdown = view.findViewById<AutoCompleteTextView>(R.id.landmarkDropdown)
        val btnSave = view.findViewById<Button>(R.id.savePlaceButton)

        btnSave.text = getString(R.string.add_place)

        DropdownHelper.populateCategories(requireContext(), categoryDropdown)
        DropdownHelper.populateLandmarks(requireContext(), landmarkDropdown)

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
                val categoryId =  DropdownHelper.getCategoryId(categoryName)
                val landmarkId = DropdownHelper.getLandmarkId(landmarkName)

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
