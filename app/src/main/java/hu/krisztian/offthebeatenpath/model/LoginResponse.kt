package hu.krisztian.offthebeatenpath.network

data class LoginResponse(
    val message: String,
    val user_id: Int,
    val user_password: String,
    val user_name: String,
    val user_admin: Int,
    val user_email: String
)
