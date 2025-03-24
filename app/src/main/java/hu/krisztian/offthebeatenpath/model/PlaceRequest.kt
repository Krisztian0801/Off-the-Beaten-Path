package hu.krisztian.offthebeatenpath.model

data class PlaceRequest(
    val poi_name: String,
    val poi_description: String,
    val latitude: Double?,
    val longitude: Double?,
    val landmark_id: Int,
    val category_id: Int,
    val user_id: Int
)

