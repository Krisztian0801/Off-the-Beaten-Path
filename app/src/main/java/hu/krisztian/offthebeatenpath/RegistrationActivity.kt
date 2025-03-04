package hu.krisztian.offthebeatenpath

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import hu.krisztian.offthebeatenpath.helpers.ApiResponseHandler
import hu.krisztian.offthebeatenpath.model.RegistrationRequest
import hu.krisztian.offthebeatenpath.network.RegistrationResponse
import hu.krisztian.offthebeatenpath.network.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {

    private lateinit var registerButton: Button
    private lateinit var registerEmailTextInput: TextInputLayout
    private lateinit var registerNameTextInput: TextInputLayout
    private lateinit var registerPasswordTextInput: TextInputLayout
    private lateinit var registerPasswordAgainTextInput: TextInputLayout
    private lateinit var apiResponseHandler: ApiResponseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registration)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        registerButton = findViewById(R.id.registerButton)
        registerEmailTextInput = findViewById(R.id.registrationEmailEditText)
        registerNameTextInput = findViewById(R.id.registrationNameEditText)
        registerPasswordTextInput = findViewById(R.id.registrationPasswordEditText)
        registerPasswordAgainTextInput = findViewById(R.id.passwordAgainEditText)


        apiResponseHandler = ApiResponseHandler(this)

        registerButton.setOnClickListener {
            val email = registerEmailTextInput.editText?.text.toString().trim()
            val name = registerNameTextInput.editText?.text.toString().trim()
            val password = registerPasswordTextInput.editText?.text.toString().trim()
            val passwordAgain = registerPasswordAgainTextInput.editText?.text.toString().trim()

            validateField(registerEmailTextInput, email, R.string.empty_textfield)
            validateField(registerNameTextInput, name, R.string.empty_textfield)
            validateField(registerPasswordTextInput, password, R.string.empty_textfield)
            validateField(registerPasswordAgainTextInput, passwordAgain, R.string.empty_textfield)
            validatePasswordMatch(password, passwordAgain)

            if (email.isEmpty() || name.isEmpty() || password.isEmpty() || passwordAgain.isEmpty() || password != passwordAgain) {
                return@setOnClickListener
            }

            registerUser(email, name, password)
        }
    }

    private fun validateField(input: TextInputLayout, value: String, errorMessageResId: Int) {
        input.error = if (value.isEmpty()) getString(errorMessageResId) else null
    }

    private fun validatePasswordMatch(password: String, passwordAgain: String) {
        val errorMessage = getString(R.string.passwords_do_not_match)
        if (password != passwordAgain) {
            registerPasswordTextInput.error = errorMessage
            registerPasswordAgainTextInput.error = errorMessage
        } else {
            registerPasswordTextInput.error = null
            registerPasswordAgainTextInput.error = null
        }
    }

    private fun registerUser(email: String, name: String, password: String) {
        val request = RegistrationRequest(email, name, password)

        RetrofitClient.registrationService.register(request)
            .enqueue(object : Callback<RegistrationResponse> {
                override fun onResponse(
                    call: Call<RegistrationResponse>,
                    response: Response<RegistrationResponse>
                ) {
                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@RegistrationActivity,
                            "Registration Successful!",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i("Register Success", "Status Code: ${response.code()}")
                        finish()
                    } else {

                        val errorJson = try {
                            JSONObject(response.errorBody()?.string() ?: "{}")
                        } catch (e: Exception) {
                            JSONObject()
                        }
                        Log.e(
                            "Register Fail",
                            "Status Code: ${response.code()}, Error: ${errorJson.toString()}"
                        )
                        apiResponseHandler.handleApiResponse(response.code(), errorJson)

                    }
                }

                override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Network Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }


}
