package hu.krisztian.offthebeatenpath

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var tryAgainButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewPlaces)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        noInternetTextView = view.findViewById(R.id.noInternetTextView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        tryAgainButton = view.findViewById(R.id.tryAgainButton)

        // 👉 Kezdetben üres adapter beállítása
        placesAdapter = PlacesAdapter(emptyList()) {
            val intent = Intent(requireActivity(), PlaceDetailActivity::class.java)
            startActivity(intent)
        }
        recyclerView.adapter = placesAdapter

        // 👉 Adatok betöltése első indításkor
        fetchPlaces()

        swipeRefreshLayout.setOnRefreshListener {
            if (hasInternet()) {
                fetchPlaces()
            } else {
                showNoInternet()
            }
        }

        tryAgainButton.setOnClickListener {
            if (hasInternet()) {
                fetchPlaces()
                hideNoInternet()
            } else {
                showNoInternet()
            }
        }

        return view
    }

    private fun fetchPlaces() {
        swipeRefreshLayout.isRefreshing = true

        RetrofitClient.placesService.getAllPOIs().enqueue(object : Callback<PlacesListResponse> {
            override fun onResponse(call: Call<PlacesListResponse>, response: Response<PlacesListResponse>) {
                swipeRefreshLayout.isRefreshing = false

                if (response.isSuccessful) {
                    val places = response.body()?.message
                    if (places != null) {
                        // 🔁 Adapter frissítése meglévő példányon
                        placesAdapter.updateData(places)
                    } else {
                        Log.e("HomeFragment", "Place not found")
                        showError(getString(R.string.no_places_available))
                    }
                } else {
                    Log.e("HomeFragment", "Error: ${response.code()}")
                    showError(getString(R.string.failed_to_load_places_please_try_again_later))
                }
            }

            override fun onFailure(call: Call<PlacesListResponse>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(requireContext(), getString(R.string.network_error, t.message), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    private fun hasInternet(): Boolean {
        return NetworkHelper.isMobileDataEnabled(requireContext()) ||
                NetworkHelper.isWiFiEnabled(requireContext()) ||
                NetworkHelper.isInternetAvailable()
    }

    private fun showNoInternet() {
        noInternetTextView.visibility = View.VISIBLE
        tryAgainButton.visibility = View.VISIBLE
    }

    private fun hideNoInternet() {
        noInternetTextView.visibility = View.GONE
        tryAgainButton.visibility = View.GONE
    }
}
