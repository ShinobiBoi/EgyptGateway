package com.besha.egyptguide.features.quiz.presentation.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.ViewState
import com.besha.egyptguide.features.quiz.data.model.QuizItem
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizResponse
import com.besha.egyptguide.features.quiz.data.model.VisitResponse

data class QuizViewState(
    val quizItems: CommonViewState<List<QuizItem>> = CommonViewState(),
    val visitResponse: CommonViewState<VisitResponse> = CommonViewState(),
    val submitQuizResponse: CommonViewState<SubmitQuizResponse> = CommonViewState()
) : ViewState
