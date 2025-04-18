package hu.krisztian.offthebeatenpath

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hu.krisztian.offthebeatenpath.adapter.PlacesAdapter
import hu.krisztian.offthebeatenpath.databinding.FragmentProfileBinding
import hu.krisztian.offthebeatenpath.model.PlacesListResponse
import hu.krisztian.offthebeatenpath.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var placesAdapter: PlacesAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Properly initialize the binding
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Access the binding to initialize RecyclerView
        binding.profileRecyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView = binding.profileRecyclerView

        loadUserData()
        fetchPlaces()

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun loadUserData() {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        val userName = sharedPreferences.getString("user_name", "Unknown User")
        val userEmail = sharedPreferences.getString("user_email", "unknown@example.com")
        val userImage = sharedPreferences.getString("user_image", null)

        Log.d("ProfileFragment", "Loaded user data: name=$userName, email=$userEmail, image=$userImage")

        binding.userNameTextView.text = userName
        binding.userEmailTextView.text = userEmail

        Glide.with(this)
            .load(userImage)
            .placeholder(R.drawable.user)
            .error(R.drawable.user)
            .into(binding.userImageView)
    }

    private fun fetchPlaces() {
        val userIdString = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE).getString("user_id", null)
        val userId = userIdString?.toIntOrNull() ?: -1
        RetrofitClient.placesService.getPOIsByUserID(userId).enqueue(object : Callback<PlacesListResponse> {
            override fun onResponse(call: Call<PlacesListResponse>, response: Response<PlacesListResponse>) {
                if (response.isSuccessful) {
                    val places = response.body()?.message
                    if (places != null) {

                        placesAdapter = PlacesAdapter(places) {
                            val intent = Intent(requireActivity(), PlaceDetailActivity::class.java)
                            startActivity(intent)
                        }
                        recyclerView.adapter = placesAdapter
                    } else {
                        Log.e("ProfileFragment", "Place not found")
                        showError(getString(R.string.no_places_available))
                    }
                } else {
                    Log.e("ProfileFragment", "Error: ${response.code()}")
                    showError(getString(R.string.failed_to_load_places_please_try_again_later))
                }
            }

            override fun onFailure(call: Call<PlacesListResponse>, t: Throwable) {
                Toast.makeText(requireContext(), getString(R.string.network_error, t.message), Toast.LENGTH_SHORT).show()
            }

        })
    }
    private fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

}
