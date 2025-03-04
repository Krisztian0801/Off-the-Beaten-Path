package hu.krisztian.offthebeatenpath.model

data class RegistrationRequest(
    val email: String,
    val username: String,
    val password: String
)