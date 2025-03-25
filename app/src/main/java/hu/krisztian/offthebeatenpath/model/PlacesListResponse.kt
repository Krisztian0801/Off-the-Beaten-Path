package hu.krisztian.offthebeatenpath.model

data class PlacesListResponse(
    val success: Boolean,
    val message: List<Place>
)
