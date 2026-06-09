package com.besha.egyptguide.features.maps.data.dto

data class RoutesDto(
    val routes: List<Route>? = null
)

data class Route(
    val distanceMeters: Int? = null,
    val duration: String? = null,
    val polyline: Polyline? = null
)