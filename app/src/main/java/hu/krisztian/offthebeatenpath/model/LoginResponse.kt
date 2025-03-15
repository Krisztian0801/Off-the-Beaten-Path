package hu.krisztian.offthebeatenpath.network

data class LoginResponse(
    val message: String,
    val user: User?
)

data class User(
    val user_id: Int,
    val user_name: String,
    val user_email: String,
    val user_admin: Int,
    val token: String
)
