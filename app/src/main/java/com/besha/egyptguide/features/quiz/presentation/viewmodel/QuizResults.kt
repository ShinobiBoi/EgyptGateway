package com.besha.egyptguide.features.quiz.presentation.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.Result
import com.besha.egyptguide.features.quiz.data.model.QuizItem

sealed class QuizResults : Result<QuizViewState> {
    data class GetQuiz(val quizItems: CommonViewState<List<QuizItem>>) : QuizResults() {
        override fun reduce(defaultState: QuizViewState, oldState: QuizViewState): QuizViewState {
            return oldState.copy(quizItems = quizItems)
        }
    }
}
