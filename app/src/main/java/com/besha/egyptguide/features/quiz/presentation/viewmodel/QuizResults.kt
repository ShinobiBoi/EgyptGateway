package com.besha.egyptguide.features.quiz.presentation.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.Result
import com.besha.egyptguide.features.quiz.data.model.QuizItem
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizResponse
import com.besha.egyptguide.features.quiz.data.model.VisitResponse

sealed class QuizResults : Result<QuizViewState> {
    data class GetQuiz(val quizItems: CommonViewState<List<QuizItem>>) : QuizResults() {
        override fun reduce(defaultState: QuizViewState, oldState: QuizViewState): QuizViewState {
            return oldState.copy(quizItems = quizItems)
        }
    }

    data class SubmitQuiz(val submitQuizResponse: CommonViewState<SubmitQuizResponse>) : QuizResults() {
        override fun reduce(
            defaultState: QuizViewState,
            oldState: QuizViewState
        ): QuizViewState {
            return oldState.copy(submitQuizResponse = submitQuizResponse)
        }
    }

    data class Visit(val visitResponse: CommonViewState<VisitResponse>) : QuizResults() {
        override fun reduce(
            defaultState: QuizViewState,
            oldState: QuizViewState
        ): QuizViewState {
            return oldState.copy(visitResponse = visitResponse)
        }
    }

}
