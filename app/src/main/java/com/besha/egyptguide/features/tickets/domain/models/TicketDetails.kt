package com.besha.egyptguide.features.tickets.domain.models

data class TicketDetails(
    val bookingDate: String?,
    val googlePlaceId: String?,
    val id: String?,
    val imageUrl: String?,
    val latitude: Double?,
    val longitude: Double?,
    val name: String?,
    val status: TicketStatus,
    val userId: String?,
    val verifiedAt: String?,
)
