package com.besha.egyptguide.features.camera.data.dto

data class RatingRequest(
    val comment: String,
    val crowd_level: String,
    val monument_id: String,
    val rating: Int
)