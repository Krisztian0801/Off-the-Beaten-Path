import hu.krisztian.offthebeatenpath.model.UpdateUserRequest
import hu.krisztian.offthebeatenpath.model.UpdateUserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    // ✅ Get user profile data (using GET request)
    @GET("user.api.php")
    fun getUserProfile(@Query("id") userId: Int): Call<UpdateUserResponse>

    @Headers("Content-Type: application/json")
    @PUT("user.api.php")
    fun updateUser(
        @Body request: UpdateUserRequest
    ): Call<UpdateUserResponse>

    // ✅ Upload image separately
    @Multipart
    @POST("upload_profile_image.php") // Use the correct API endpoint
    fun uploadProfileImage(
        @Part("id") id: RequestBody,
        @Part profileImage: MultipartBody.Part
    ): Call<UpdateUserResponse>

    // ✅ Delete user account (DELETE request)
    @DELETE("user.api.php")
    fun deleteUser(@Body request: UpdateUserRequest): Call<UpdateUserResponse>
}
