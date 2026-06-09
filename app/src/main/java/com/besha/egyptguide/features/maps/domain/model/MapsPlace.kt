package com.besha.egyptguide.features.maps.domain.model

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

data class MapsPlace(
    val id: String?,
    val displayName: String?,
    val formattedAddress: String?,
    val location: LatLng?,
    val imageUri :  Uri?,
    val distanceMeters: Int? = null,
    val duration: String? = null,
)
