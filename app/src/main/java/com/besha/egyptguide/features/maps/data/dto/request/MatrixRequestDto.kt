package com.besha.egyptguide.features.maps.data.dto.request

data class MatrixRequestDto(
    val destinations: List<DestinationX>? = listOf(),
    val origins: List<OriginX>? = listOf(),
    val travelMode: TravelMode? = TravelMode.DRIVE
)