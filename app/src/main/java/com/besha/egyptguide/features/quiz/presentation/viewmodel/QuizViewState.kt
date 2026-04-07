package com.besha.egyptguide.features.quiz.presentation.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.ViewState
import com.besha.egyptguide.features.quiz.data.model.QuizItem

data class QuizViewState(
    val quizItems: CommonViewState<List<QuizItem>> = CommonViewState()
) : ViewState
