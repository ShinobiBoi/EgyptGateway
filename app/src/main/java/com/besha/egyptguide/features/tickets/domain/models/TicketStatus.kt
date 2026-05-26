package com.besha.egyptguide.features.tickets.domain.models

enum class TicketStatus(val value: String) {
    PENDING("pending"),
    APPROVED("approved"),
    DECLINED("declined"),
    UNKNOWN("unknown");

    companion object {

        operator fun invoke(value: String) : TicketStatus {
            return entries.find {
                it.value.equals(value, ignoreCase = true)
            } ?: UNKNOWN
        }
    }
}