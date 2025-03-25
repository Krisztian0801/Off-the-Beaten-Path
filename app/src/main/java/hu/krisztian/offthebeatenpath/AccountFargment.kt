package hu.krisztian.offthebeatenpath

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import hu.krisztian.offthebeatenpath.model.UpdateUserRequest
import hu.krisztian.offthebeatenpath.model.UpdateUserResponse
import hu.krisztian.offthebeatenpath.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class AccountFragment : Fragment() {

    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        loadUserData(view)

        editTextName = view.findViewById(R.id.editTextName)
        editTextEmail = view.findViewById(R.id.editTextEmail)

        editTextName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) updateUserProfile(editTextName.text.toString(), null, null)
        }
        editTextEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) updateUserProfile(null, editTextEmail.text.toString(), null)
        }

//        view.findViewById<MaterialButton>(R.id.buttonChangeProfilePicture).setOnClickListener {
//            selectProfileImage()
//        }
        view.findViewById<MaterialButton>(R.id.buttonChangePassword).setOnClickListener {
           changeUserPassword(R.id.editOldPassword.toString(), R.id.editNewPassword.toString())
        }
        view.findViewById<MaterialButton>(R.id.buttonDeleteAccount).setOnClickListener {
            confirmDeleteAccount()
        }

        return view
    }

    private fun updateUserProfile(username: String?, email: String?, imageUri: Uri?) {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)?.toIntOrNull()

        if (userId == null) {
            Toast.makeText(requireContext(), "User ID not found!", Toast.LENGTH_SHORT).show()
            return
        }

        val request = UpdateUserRequest(id = userId, username = username, email = email)
        RetrofitClient.userService.updateUser(request)
            .enqueue(object : Callback<UpdateUserResponse> {
                override fun onResponse(call: Call<UpdateUserResponse>, response: Response<UpdateUserResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        username?.let { saveLocalUserData("user_name", it) }
                        email?.let { saveLocalUserData("user_email", it) }
                        Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show()

                        imageUri?.let { uploadProfileImage(userId, it) }
                    } else {
                        Toast.makeText(requireContext(), "Failed to update profile!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun uploadProfileImage(userId: Int, imageUri: Uri) {
        val filePath = getPathFromUri(imageUri)

        val file = File(filePath)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("profileImage", file.name, requestFile)
        val idPart = userId.toString().toRequestBody(MultipartBody.FORM)

        RetrofitClient.userService.uploadProfileImage(idPart, imagePart)
            .enqueue(object : Callback<UpdateUserResponse> {
                override fun onResponse(call: Call<UpdateUserResponse>, response: Response<UpdateUserResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(requireContext(), "Profile image updated!", Toast.LENGTH_SHORT).show()
                        Log.d("UPLOAD", "Uploading file: ${file.name}, Size: ${file.length()} bytes")
                        Log.d("UPLOAD", "User ID: $userId")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(requireContext(), "Failed to upload image!", Toast.LENGTH_SHORT).show()
                        Log.e("API Error", "Failed to upload image: $errorBody")
                    }
                }

                override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }



    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            updateUserProfile(null, null, uri)
        } else {
            Toast.makeText(requireContext(), "Image selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }
    private fun selectProfileImage() {
        pickImageLauncher.launch("image/*")
    }
    private fun getPathFromUri(uri: Uri): String {
        var filePath = ""
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            val index = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            filePath = it.getString(index)
        }
        return filePath
    }



    private fun saveLocalUserData(key: String, value: String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    private fun loadUserData(view: View) {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        view.findViewById<TextInputEditText>(R.id.editTextName).setText(sharedPreferences.getString("user_name", ""))
        view.findViewById<TextInputEditText>(R.id.editTextEmail).setText(sharedPreferences.getString("user_email", ""))
    }


    private fun changeUserPassword(oldPassword: String, newPassword: String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)?.toIntOrNull()

        if (userId == null) {
            Toast.makeText(requireContext(), "User ID not found!", Toast.LENGTH_SHORT).show()
            return
        }

        val request = UpdateUserRequest(
            id = userId,
            oldPassword = oldPassword,
            newPassword = newPassword
        )

        RetrofitClient.userService.updateUser(request)
            .enqueue(object : Callback<UpdateUserResponse> {
                override fun onResponse(call: Call<UpdateUserResponse>, response: Response<UpdateUserResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(requireContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to update password!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }



    private fun confirmDeleteAccount() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Yes, delete") { _, _ ->
                deleteUserAccount()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteUserAccount() {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)?.toIntOrNull()

        if (userId == null) {
            Toast.makeText(requireContext(), "User ID not found!", Toast.LENGTH_SHORT).show()
            return
        }

        val request = UpdateUserRequest(id = userId)

        RetrofitClient.userService.deleteUser(request).enqueue(object : Callback<UpdateUserResponse> {
            override fun onResponse(call: Call<UpdateUserResponse>, response: Response<UpdateUserResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(requireContext(), "Account deleted successfully!", Toast.LENGTH_SHORT).show()
                    logoutAndRedirect()
                } else {
                    Toast.makeText(requireContext(), "Failed to delete account!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun logoutAndRedirect() {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}
