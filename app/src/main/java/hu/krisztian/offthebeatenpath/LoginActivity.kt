package hu.krisztian.offthebeatenpath

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.textfield.TextInputLayout
import hu.krisztian.offthebeatenpath.network.User
import hu.krisztian.offthebeatenpath.network.LoginRequest
import hu.krisztian.offthebeatenpath.network.LoginResponse
import hu.krisztian.offthebeatenpath.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var registrationButton: Button
    private lateinit var emailEditText: TextInputLayout
    private lateinit var passwordEditText: TextInputLayout
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_login)

        registrationButton = findViewById(R.id.registerButton)
        loginButton = findViewById(R.id.loginButton)
        emailEditText = findViewById(R.id.loginEmailEditText)
        passwordEditText = findViewById(R.id.loginPasswordEditText)

        registrationButton.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        loginButton.setOnClickListener {
            val email = emailEditText.editText?.text.toString().trim()
            val password = passwordEditText.editText?.text.toString().trim()

            if (email.isEmpty()) {
                emailEditText.error = getString(R.string.empty_textfield)
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordEditText.error = getString(R.string.empty_textfield)
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        val request = LoginRequest(email, password)

        RetrofitClient.loginService.login(request)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val user = response.body()?.user

                        if (user != null) {
                            Log.d("LoginActivity", "User: ${user.user_name}, Token: ${user.token}")
                            saveUserSession(user)

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("LoginActivity", "User object is null")
                            Toast.makeText(this@LoginActivity,
                                getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val rawJson = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("LoginActivity", "Malformed JSON: $rawJson")
                        Toast.makeText(this@LoginActivity,
                            getString(R.string.invalid_email_or_password), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("API Error", "Error: ${t.message}")
                    Toast.makeText(this@LoginActivity, getString(R.string.network_error, t.message), Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun saveUserSession(user: User) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("user_id", user.user_id.toString())
            putString("user_name", user.user_name)
            putString("user_email", user.user_email)
            putInt("isAdmin", user.user_admin)
            putString("auth_token", user.token)
            apply()
        }
    }
}
