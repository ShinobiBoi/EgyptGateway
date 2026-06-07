package com.besha.egyptguide.features.monuments.data.dto

data class MonumentDto(
    val city: String? = null,
    val description: String? = null,
    val id: String? = null,
    val images: List<String?>? = null,
    val label_id: String? = null,
    val latitude: Double? = null,
    val location: String? = null,
    val longitude: Double? = null,
    val name: String? = null,
)