package hu.krisztian.offthebeatenpath.model

data class CoordinateResponse(
    val success: Boolean,
    val message: Coordinate
)

data class Coordinate(
    val coordinate_id: Int,
    val coordinate_latitude: Double,
    val coordinate_longitude: Double
)
