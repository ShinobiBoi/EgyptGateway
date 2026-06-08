package com.besha.egyptguide.features.monuments.data.mapper

import com.besha.egyptguide.features.monuments.data.dto.RatingDto
import com.besha.egyptguide.features.monuments.domain.model.Rating
import com.besha.egyptguide.features.tickets.data.mapper.toFormattedDate

fun RatingDto.toDomain(): Rating {
    return Rating(
        comment = comment,
        crowd_level = crowd_level,
        formattedDate = date?.toFormattedDate(),
        id = id,
        monument_id = monument_id,
        rating = rating,
        user_id = user_id,
        user_name = user_name,
        user_photo_url = user_photo_url
    )
}