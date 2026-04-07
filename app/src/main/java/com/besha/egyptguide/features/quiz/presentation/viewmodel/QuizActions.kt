package com.besha.egyptguide.features.quiz.presentation.viewmodel

import com.besha.egyptguide.appcore.mvi.Action

sealed class QuizActions : Action {
    data class GetQuiz(val monumentId: String) : QuizActions()
}
