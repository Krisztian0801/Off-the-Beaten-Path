package hu.krisztian.offthebeatenpath

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.krisztian.offthebeatenpath.adapter.PlacesAdapter
import hu.krisztian.offthebeatenpath.model.PlaceResponse
import hu.krisztian.offthebeatenpath.network.NetworkHelper
import hu.krisztian.offthebeatenpath.network.RetrofitClient
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
        RetrofitClient.placesService.getPOIs().enqueue(object : Callback<PlaceResponse> {
            override fun onResponse(
                call: Call<PlaceResponse>,
                response: Response<PlaceResponse>
            ) {
                if (response.isSuccessful) {
                    val places = response.body()?.message ?: emptyList()
                    placesAdapter = PlacesAdapter(places) { place ->
                        val intent = Intent(activity, PlaceDetailActivity::class.java)
                        intent.putExtra("place_id", place.poi_id)
                        startActivity(intent)
                    }
                    recyclerView.adapter = placesAdapter
                }
            }

            override fun onFailure(call: Call<PlaceResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
