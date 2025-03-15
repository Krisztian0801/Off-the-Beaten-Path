package hu.krisztian.offthebeatenpath.model

data class UserProfileUpdate(
    val username: String? = null,
    val email: String? = null,
    val oldPassword: String? = null,
    val newPassword: String? = null
)