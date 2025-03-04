package hu.krisztian.offthebeatenpath

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
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
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        registrationButton = findViewById(R.id.registerButton)
        loginButton = findViewById(R.id.loginButton)
        emailEditText = findViewById(R.id.loginEmailEditText)
        passwordEditText = findViewById(R.id.loginPasswordEditText)

        registrationButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.editText?.text.toString().trim()
            val password = passwordEditText.editText?.text.toString().trim()

            if (email.isEmpty()) {
                emailEditText.error = getString(R.string.empty_textfield)
                return@setOnClickListener
            } else {
                emailEditText.error = null
            }

            if (password.isEmpty()) {
                passwordEditText.error = getString(R.string.empty_textfield)
                return@setOnClickListener
            } else {
                passwordEditText.error = null
            }

            RetrofitClient.loginService.login(LoginRequest(email, password))
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful && response.body() != null) {
                            val loginResponse = response.body()
                            Toast.makeText(this@LoginActivity, "Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show()

                            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                putString("user_id", loginResponse!!.user_id.toString())
                                putString("user_password", loginResponse.user_password)
                                putString("user_name", loginResponse.user_name)
                                putBoolean("isAdmin", loginResponse.user_admin)
                                apply()
                            }

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Hibás email vagy jelszó!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "Hálózati hiba: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("Hálózati hiba: ", "${t.message}")
                    }
                })
        }
    }
}
