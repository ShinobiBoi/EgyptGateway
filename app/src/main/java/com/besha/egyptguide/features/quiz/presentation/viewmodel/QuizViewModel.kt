package com.besha.egyptguide.features.quiz.presentation.viewmodel

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.MVIBaseViewModel
import com.besha.egyptguide.features.quiz.domain.usecase.GetQuizUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getQuizUseCase: GetQuizUseCase
) : MVIBaseViewModel<QuizActions, QuizResults, QuizViewState>() {

    override val defaultViewState: QuizViewState
        get() = QuizViewState()

    override fun handleAction(action: QuizActions): Flow<QuizResults> = flow {
        when (action) {
            is QuizActions.GetQuiz -> {
                emit(QuizResults.GetQuiz(CommonViewState(isLoading = true)))
                val result = getQuizUseCase(action.monumentId)
                when (result) {
                    is DataState.Success -> {
                        emit(QuizResults.GetQuiz(CommonViewState(data = result.data)))
                    }
                    is DataState.Error -> {
                        emit(QuizResults.GetQuiz(CommonViewState(errorThrowable = Exception(result.throwable.message))))
                    }
                    else -> {
                        emit(QuizResults.GetQuiz(CommonViewState(isLoading = false)))
                    }
                }
            }
        }
    }
}
