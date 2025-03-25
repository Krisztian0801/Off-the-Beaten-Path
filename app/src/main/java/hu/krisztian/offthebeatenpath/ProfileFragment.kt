package hu.krisztian.offthebeatenpath

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import hu.krisztian.offthebeatenpath.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        loadUserData()

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

}
