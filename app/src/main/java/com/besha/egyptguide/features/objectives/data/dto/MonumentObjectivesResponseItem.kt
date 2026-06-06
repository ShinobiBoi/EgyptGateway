package com.besha.egyptguide.features.objectives.data.dto

data class MonumentObjectivesResponseItem(
    val completed: Boolean? = null,
    val completion_date: Any? = null,
    val current_progress: String? = null,
    val description: String? = null,
    val id: String? = null,
    val monuments: List<Monument?>? = null,
    val points_reward: Int? = null,
    val status: String? = null,
    val title: String? = null,
    val total_monuments: Int? = null,
    val visited_count: Int? = null
)