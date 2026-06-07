package com.besha.egyptguide.features.monuments.data.dto

data class RatingSummaryDto(
    val average_rating: Double? = null,
    val monument_id: String? = null,
    val rating_breakdown: RatingBreakdown? = null,
    val ratings_count: Int? = null
)