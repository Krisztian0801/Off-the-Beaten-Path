package hu.krisztian.offthebeatenpath.model

data class PlaceResponse(
    val success: Boolean,
    val message: Place
)

data class Place(
    val poi_id: Int,
    val poi_name: String,
    val poi_description: String?,
    val coordinate_id: Int,
    val landmark_id: Int,
    val category_id: Int,
    val user_id: Int
)


