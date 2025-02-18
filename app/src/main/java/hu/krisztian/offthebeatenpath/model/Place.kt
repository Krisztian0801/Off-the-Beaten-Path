package hu.krisztian.offthebeatenpath.model


data class Place (
    val name: String,
    val category: String,
    val location: Location,
    val landmark: String,
    val discription: String,
    val user: User
)
data class Location (
    val latitude: Double,
    val longitude: Double
)
data class User (
    val id: Int,
    val name: String
)