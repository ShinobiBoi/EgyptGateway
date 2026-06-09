package com.besha.egyptguide.features.objectives.data.dto

import com.google.gson.annotations.SerializedName

data class MonumentObjectivesDto(
    val completed: Boolean? = null,
    val completion_date: Any? = null,
    val current_progress: String? = null,
    val description: String? = null,
    val id: String? = null,
    @SerializedName("monuments")
    val monumentObjectivesItems: List<MonumentObjectivesItem?>? = null,
    val points_reward: Int? = null,
    val status: String? = null,
    val title: String? = null,
    val total_monuments: Int? = null,
    val visited_count: Int? = null
)