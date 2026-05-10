package com.besha.egyptguide.features.quiz.presentation.viewmodel

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.MVIBaseViewModel
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizRequest
import com.besha.egyptguide.features.quiz.data.model.VisitRequest
import com.besha.egyptguide.features.quiz.domain.usecase.GetQuizUseCase
import com.besha.egyptguide.features.quiz.domain.usecase.SubmitQuizUseCase
import com.besha.egyptguide.features.quiz.domain.usecase.VisitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getQuizUseCase: GetQuizUseCase,
    private val submitQuizUseCase: SubmitQuizUseCase,
    private val visitUseCase: VisitUseCase,
) : MVIBaseViewModel<QuizActions, QuizResults, QuizViewState>() {

    override val defaultViewState: QuizViewState
        get() = QuizViewState()

    override fun handleAction(action: QuizActions): Flow<QuizResults> = flow {
        when (action) {
            is QuizActions.GetQuiz -> {
                handleGetQuiz(this,action.monumentId)
            }
            is QuizActions.SubmitQuiz -> {
                handleSubmitQuiz(this,action.submitQuizRequest)
            }

            is QuizActions.Visit -> {
                handleVisit(this,action.visitRequest)
            }
        }
    }


    private suspend fun handleVisit(
        collector: FlowCollector<QuizResults>,
        visitRequest: VisitRequest
    ) {

        collector.emit(QuizResults.Visit(CommonViewState(isLoading = true)))
        val result = visitUseCase(visitRequest)
        when (result) {
            is DataState.Success-> {

                collector.emit(QuizResults.Visit(CommonViewState(data = result.data)))
            }
            is DataState.Error -> {
                collector.emit(QuizResults.Visit(CommonViewState(errorThrowable = Exception(result.throwable.message))))
            }
            else -> {
                collector.emit(QuizResults.Visit(CommonViewState(isLoading = false)))
            }
        }


    }

    private suspend fun handleSubmitQuiz(
        collector: FlowCollector<QuizResults>,
        submitQuizRequest: SubmitQuizRequest
    ) {
        collector.emit(QuizResults.SubmitQuiz(CommonViewState(isLoading = true)))
        val result = submitQuizUseCase(submitQuizRequest)
        when (result) {
            is DataState.Success-> {

                collector.emit(QuizResults.SubmitQuiz(CommonViewState(data = result.data)))
            }
            is DataState.Error -> {
                collector.emit(QuizResults.SubmitQuiz(CommonViewState(errorThrowable = Exception(result.throwable.message))))
            }
            else -> {
                collector.emit(QuizResults.SubmitQuiz(CommonViewState(isLoading = false)))
            }
        }
    }

    private suspend fun handleGetQuiz(
        collector: FlowCollector<QuizResults>,
        monumentId: String
    ) {

        collector.emit(QuizResults.GetQuiz(CommonViewState(isLoading = true)))
        val result = getQuizUseCase(monumentId)
        when (result) {
            is DataState.Success -> {
                collector.emit(QuizResults.GetQuiz(CommonViewState(data = result.data)))
            }
            is DataState.Error -> {
                collector.emit(QuizResults.GetQuiz(CommonViewState(errorThrowable = Exception(result.throwable.message))))
            }
            else -> {
               collector.emit(QuizResults.GetQuiz(CommonViewState(isLoading = false)))
            }
        }
    }
}
