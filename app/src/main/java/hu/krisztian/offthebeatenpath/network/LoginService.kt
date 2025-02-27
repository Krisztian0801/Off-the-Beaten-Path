package hu.krisztian.offthebeatenpath.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("login.api.php")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
