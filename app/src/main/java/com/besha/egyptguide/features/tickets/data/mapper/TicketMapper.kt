package com.besha.egyptguide.features.tickets.data.mapper

import com.besha.egyptguide.features.tickets.data.dto.GetTicketResponseItem
import com.besha.egyptguide.features.tickets.data.dto.TicketDetailsResponse
import com.besha.egyptguide.features.tickets.domain.models.Ticket
import com.besha.egyptguide.features.tickets.domain.models.TicketDetails
import com.besha.egyptguide.features.tickets.domain.models.TicketStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun GetTicketResponseItem.toDomain(): Ticket {
    return Ticket(
        bookingDate = booking_date?.toFormattedDate(),
        id = id,
        name = name,
        status = TicketStatus(status?:"unkown"),
    )
}

fun TicketDetailsResponse.toDomain(): TicketDetails{
    return TicketDetails(
        bookingDate = booking_date?.toFormattedDate(),
        googlePlaceId = google_place_id,
        id = id,
        imageUrl = image_url,
        latitude = latitude,
        longitude = longitude,
        name = name,
        status = TicketStatus(status?:"unkown"),
        userId = user_id,
        verifiedAt = verified_at,
    )
}

fun String.toFormattedDate(): String {
    return try {

        val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        val outputFormatter = DateTimeFormatter.ofPattern(
            "dd MMM yyyy • hh:mm a",
            Locale.getDefault()
        )

        val date = LocalDateTime.parse(this, inputFormatter)

        outputFormatter.format(date)

    } catch (e: Exception) {
        this
    }
}