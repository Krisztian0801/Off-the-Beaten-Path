package hu.krisztian.offthebeatenpath.network

import hu.krisztian.offthebeatenpath.model.PlaceRequest
import hu.krisztian.offthebeatenpath.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.*

interface PlaceService {

    // Get all POIs or a single POI by ID
    @GET("places.api.php")
    fun getPOIs(@Query("id") poiId: Int? = null): Call<PlaceResponse>

    // Add a new POI
    @POST("places.api.php")
    fun addPOI(@Body placeRequest: PlaceRequest): Call<PlaceResponse>

    // Update an existing POI
    @PUT("places.api.php")
    fun updatePOI(@Body placeRequest: PlaceRequest): Call<PlaceResponse>

    // Delete a POI by ID
    @DELETE("places.api.php")
    fun deletePOI(@Query("id") poiId: Int): Call<PlaceResponse>
}
