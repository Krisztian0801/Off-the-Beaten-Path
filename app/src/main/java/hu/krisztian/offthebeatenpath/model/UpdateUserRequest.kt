package hu.krisztian.offthebeatenpath.model


data class UpdateUserRequest(
    val id: Int,
    val username: String? = null,
    val email: String? = null,
    val oldPassword: String? = null,
    val newPassword: String? = null,
    val profileImage: String? = null // Base64 encoded image (optional)
)

