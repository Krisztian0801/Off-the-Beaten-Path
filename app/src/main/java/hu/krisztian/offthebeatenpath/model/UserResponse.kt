package hu.krisztian.offthebeatenpath.model

data class UserResponse(
    val success: Boolean,
    val message: String,
    val user: User?
)

data class User(
    val user_id: Int? = null,
    val user_name: String? = null,
    val user_email: String? = null,

)
