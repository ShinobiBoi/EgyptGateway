package com.besha.egyptguide.features.quiz.data.model

data class SubmitQuizResponse(
    val earned_points: Int,
    val message: String,
    val total_points: Int
)