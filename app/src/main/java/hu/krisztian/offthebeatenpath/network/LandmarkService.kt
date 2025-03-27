package hu.krisztian.offthebeatenpath.network


import hu.krisztian.offthebeatenpath.model.Landmark
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LandmarkService {

    @GET("landmark.api.php")
    suspend fun getLandmark(@Query("id") categoryId: Int?): Landmark
    @GET("landmark.api.php")
    fun getLandmarks(): Call<List<Landmark>>
}