package hu.krisztian.offthebeatenpath.network

import hu.krisztian.offthebeatenpath.model.RegistrationRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationService {
    @POST("registration.api.php")
    fun register(@Body request: RegistrationRequest): Call<RegistrationResponse>
}
