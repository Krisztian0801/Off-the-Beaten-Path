package hu.krisztian.offthebeatenpath.network

import hu.krisztian.offthebeatenpath.model.Category
import hu.krisztian.offthebeatenpath.model.CategoryResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CategoryService {
    @GET("category.api.php")
    fun getCategory(@Query("id") categoryId: Int?): Call<CategoryResponse>
    @GET("category.api.php")
    fun getCategories(): Call<List<Category>>

}

