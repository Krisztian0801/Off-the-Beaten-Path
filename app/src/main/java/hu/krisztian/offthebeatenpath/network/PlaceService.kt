package hu.krisztian.offthebeatenpath.network

import hu.krisztian.offthebeatenpath.model.PlaceRequest
import hu.krisztian.offthebeatenpath.model.PlaceResponse
import hu.krisztian.offthebeatenpath.model.PlacesListResponse
import retrofit2.Call
import retrofit2.http.*

interface PlaceService {

    @GET("places.api.php")
    fun getAllPOIs(): Call<PlacesListResponse>

    @GET("places.api.php")
    fun getPOIById(@Query("id") poiId: Int): Call<PlaceResponse>

    @POST("places.api.php")
    fun addPOI(@Body placeRequest: PlaceRequest): Call<PlaceResponse>

    @PUT("places.api.php")
    fun updatePOI(@Body placeRequest: PlaceRequest): Call<PlaceResponse>

    @DELETE("places.api.php")
    fun deletePOI(@Query("id") poiId: Int): Call<PlaceResponse>
}
