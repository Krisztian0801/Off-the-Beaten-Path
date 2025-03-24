package hu.krisztian.offthebeatenpath.network

import hu.krisztian.offthebeatenpath.model.CategoryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CategoryService {
    @GET("category.api.php")
    suspend fun getCategory(@Query("id") categoryId: String): CategoryResponse
}
