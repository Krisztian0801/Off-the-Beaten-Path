package hu.krisztian.offthebeatenpath.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://banki13.komarom.net/2024/off-the-beaten-path/api/"

    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    val loginService: LoginService by lazy { retrofit.create(LoginService::class.java) }
    val registrationService: RegistrationService by lazy { retrofit.create(RegistrationService::class.java) }
    val userService: UserService by lazy { retrofit.create(UserService::class.java) }
    val placesService: PlaceService by lazy { retrofit.create(PlaceService::class.java) }
    val categoryService: CategoryService by lazy { retrofit.create(CategoryService::class.java) }
}
