package hu.krisztian.offthebeatenpath.network

import hu.krisztian.offthebeatenpath.model.CoordinateResponse
import hu.krisztian.offthebeatenpath.model.PlaceRequest
import hu.krisztian.offthebeatenpath.model.PlaceResponse
import hu.krisztian.offthebeatenpath.model.PlaceUpdateResponse
import hu.krisztian.offthebeatenpath.model.PlacesListResponse
import retrofit2.Call
import retrofit2.http.*

interface PlaceService {

    @GET("places.api.php")
    fun getAllPOIs(): Call<PlacesListResponse>

    @GET("places.api.php")
    fun getPOIById(@Query("id") poiId: Int): Call<PlaceResponse>

    @GET("places.api.php")
    fun getPOIsByUserID(@Query("user_id") userId: Int): Call<PlacesListResponse>

    @POST("places.api.php")
    fun addPOI(@Body placeRequest: PlaceRequest): Call<PlaceResponse>

    @PUT("places.api.php")
    fun updatePOI(@Query("poi_id") poiId: Int, @Body placeRequest: PlaceRequest): Call<PlaceUpdateResponse>

    @DELETE("places.api.php")
    fun deletePOI(@Query("id") poiId: Int): Call<PlaceResponse>

    @GET("coordinates.api.php") // New endpoint to fetch coordinates
    fun getCoordinates(@Query("id") coordinateId: Int): Call<CoordinateResponse>
}

