package hu.krisztian.offthebeatenpath.network

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import hu.krisztian.offthebeatenpath.R
import hu.krisztian.offthebeatenpath.model.Category
import hu.krisztian.offthebeatenpath.model.Landmark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object DropdownHelper {

    fun populateCategories(context: Context, categoryDropdown: AutoCompleteTextView) {
        RetrofitClient.categoryService.getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    response.body()?.let { categories ->
                        val categoryNames = categories.map { it.category_name }
                        val adapter = ArrayAdapter(
                            context,
                            android.R.layout.simple_dropdown_item_1line,
                            categoryNames
                        )
                        categoryDropdown.setAdapter(adapter)
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.error_fetch_categories), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(context, "${context.getString(R.string.error_fetch_categories)}: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun populateLandmarks(context: Context, landmarkDropdown: AutoCompleteTextView) {
        RetrofitClient.landmarkService.getLandmarks().enqueue(object : Callback<List<Landmark>> {
            override fun onResponse(call: Call<List<Landmark>>, response: Response<List<Landmark>>) {
                if (response.isSuccessful) {
                    response.body()?.let { landmarks ->
                        val landmarkNames = landmarks.map { it.landmark_discription }
                        val adapter = ArrayAdapter(
                            context,
                            android.R.layout.simple_dropdown_item_1line,
                            landmarkNames
                        )
                        landmarkDropdown.setAdapter(adapter)
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.error_fetch_landmarks), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Landmark>>, t: Throwable) {
                Toast.makeText(context, "${context.getString(R.string.error_fetch_landmarks)}: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    suspend fun getCategoryId(categoryName: String): Int {
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

    suspend fun getLandmarkId(landmarkName: String): Int {
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
}