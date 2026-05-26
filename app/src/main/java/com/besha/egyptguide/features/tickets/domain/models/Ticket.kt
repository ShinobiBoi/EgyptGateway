package com.besha.egyptguide.features.tickets.domain.models

data class Ticket(
    val bookingDate: String?,
    val id: String?,
    val name: String?,
    val status: TicketStatus,
)
