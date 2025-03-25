package hu.krisztian.offthebeatenpath

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.krisztian.offthebeatenpath.adapter.PlacesAdapter
import hu.krisztian.offthebeatenpath.network.NetworkHelper
import hu.krisztian.offthebeatenpath.network.RetrofitClient
import hu.krisztian.offthebeatenpath.model.PlacesListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var placesAdapter: PlacesAdapter
    private lateinit var noInternetTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewPlaces)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        noInternetTextView = view.findViewById(R.id.noInternetTextView)

        if (NetworkHelper.isMobileDataEnabled(requireContext())) {
            fetchPlaces()
        } else {
            noInternetTextView.visibility = View.VISIBLE
        }

        return view
    }

    private fun fetchPlaces() {
        RetrofitClient.placesService.getAllPOIs().enqueue(object : Callback<PlacesListResponse> {
            override fun onResponse(call: Call<PlacesListResponse>, response: Response<PlacesListResponse>) {
                if (response.isSuccessful) {
                    val places = response.body()?.message // Extract single Place object
                    if (places != null) {

                        placesAdapter = PlacesAdapter(places) { selectedPlace ->
                            val intent = Intent(requireActivity(), PlaceDetailActivity::class.java)
                            startActivity(intent)
                        }
                        recyclerView.adapter = placesAdapter
                    } else {
                        Log.e("HomeFragment", "Place not found")
                        showError("No places available.")
                    }
                } else {
                    Log.e("HomeFragment", "Error: ${response.code()}")
                    showError("Failed to load places. Please try again later.")
                }
            }

            override fun onFailure(call: Call<PlacesListResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }




    private fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }


}
