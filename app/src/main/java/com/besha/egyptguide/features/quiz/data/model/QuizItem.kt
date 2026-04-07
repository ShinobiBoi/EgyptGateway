package com.besha.egyptguide.features.quiz.data.model

data class QuizItem(
    val correct_answer: String? = null,
    val id: String? = null,
    val monument_id: String? = null,
    val options: List<String?>? = null,
    val question: String? = null
)