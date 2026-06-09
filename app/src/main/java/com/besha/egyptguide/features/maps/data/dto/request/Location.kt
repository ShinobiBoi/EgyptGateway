package com.besha.egyptguide.features.maps.data.dto.request


data class Location(
    val latLng: MyLatLng
)

data class MyLatLng (
    val latitude: Double,
    val longitude: Double
)




