package com.besha.egyptguide.features.tickets.data.model

data class Ticket(
    val id: String,
    val monumentName: String,
    val status: TicketStatus,
    val date: String,
    val imageUrl: String? = null
)

enum class TicketStatus {
    PENDING, APPROVED, DECLINED
}
