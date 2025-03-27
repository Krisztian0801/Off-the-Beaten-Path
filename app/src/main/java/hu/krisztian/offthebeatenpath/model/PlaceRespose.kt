package hu.krisztian.offthebeatenpath.model

data class PlaceResponse(
    val success: Boolean,
    val message: Place
)

data class Place(
    val poi_id: Int,
    val poi_name: String,
    val poi_discription: String?,
    val coordinate_id: Int,  // We need to fetch coordinates separately
    val landmark_id: Int,
    val category_id: Int,
    val user_id: Int,
    var latitude: Double? = null,  // Add fields to store coordinates
    var longitude: Double? = null
)
