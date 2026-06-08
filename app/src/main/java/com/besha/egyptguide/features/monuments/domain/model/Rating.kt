package com.besha.egyptguide.features.monuments.domain.model

data class Rating(
    val comment: String? = null,
    val crowd_level: String? = null,
    val formattedDate: String? = null,
    val id: String? = null,
    val monument_id: String? = null,
    val rating: Int? = null,
    val user_id: String? = null,
    val user_name: String? = null,
    val user_photo_url : String? = null
)
