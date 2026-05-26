package com.besha.egyptguide.features.quiz.presentation.viewmodel

import com.besha.egyptguide.appcore.mvi.Action
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizRequest

sealed class QuizActions : Action {
    data class GetQuiz(val monumentId: String) : QuizActions()

    data class SubmitQuiz(val submitQuizRequest: SubmitQuizRequest) : QuizActions()

}
