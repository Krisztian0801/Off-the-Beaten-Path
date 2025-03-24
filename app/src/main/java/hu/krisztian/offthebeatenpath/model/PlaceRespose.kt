package hu.krisztian.offthebeatenpath.model

data class PlaceResponse(
    val success: Boolean,
    val message: List<Place>
)

data class Place(
    val poi_id: String,
    val poi_name: String,
    val poi_description: String?,
    val coordinate_id: String,
    val landmark_id: String,
    val category_id: String,
    val user_id: String
)

