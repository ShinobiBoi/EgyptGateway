package com.besha.egyptguide.features.maps.data.dto.request

data class RoutesRequestDto(
    val destination: Destination? = Destination(),
    val origin: Origin? = Origin(),
    val travelMode: TravelMode? = TravelMode.DRIVE
)
