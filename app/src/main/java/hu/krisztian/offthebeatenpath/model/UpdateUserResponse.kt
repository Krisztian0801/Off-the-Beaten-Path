package hu.krisztian.offthebeatenpath.model

data class UpdateUserResponse(
    val success: Boolean,
    val message: String,
    val updatedFields: UpdatedFields?
)

data class UpdatedFields(
    val username: String? = null,
    val email: String? = null,
    val profileImage: String? = null,
    val passwordUpdated: Boolean? = null
)
