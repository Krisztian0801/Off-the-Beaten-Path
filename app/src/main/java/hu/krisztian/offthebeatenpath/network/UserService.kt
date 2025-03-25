package hu.krisztian.offthebeatenpath.network

import hu.krisztian.offthebeatenpath.model.UpdateUserRequest
import hu.krisztian.offthebeatenpath.model.UpdateUserResponse
import hu.krisztian.offthebeatenpath.model.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserService {


    @GET("user.api.php")
    fun getUserProfile(@Query("id") userId: Int): Call<UserResponse>

    @Headers("Content-Type: application/json")
    @PUT("user.api.php")
    fun updateUser(
        @Body request: UpdateUserRequest
    ): Call<UpdateUserResponse>

    //This part is not working yet.
    @Multipart
    @POST("upload_profile_image.php")
    fun uploadProfileImage(
        @Part("id") id: RequestBody,
        @Part profileImage: MultipartBody.Part
    ): Call<UpdateUserResponse>

    @DELETE("user.api.php")
    fun deleteUser(@Body request: UpdateUserRequest): Call<UpdateUserResponse>
}
