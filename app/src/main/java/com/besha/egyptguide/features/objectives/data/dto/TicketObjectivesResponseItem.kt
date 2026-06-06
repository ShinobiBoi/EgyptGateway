package com.besha.egyptguide.features.objectives.data.dto

data class TicketObjectivesResponseItem(
    val approved_count: Int? = null,
    val completed: Boolean? = null,
    val completion_date: String? = null,
    val current_progress: String? = null,
    val description: String? = null,
    val id: String? = null,
    val points_reward: Int? = null,
    val required_tickets: Int? = null,
    val status: String? = null,
    val title: String? = null
)