package com.besha.egyptguide.features.quiz.data.repo

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.quiz.data.model.Quiz
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizRequest
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizResponse
import com.besha.egyptguide.features.quiz.data.model.VisitRequest
import com.besha.egyptguide.features.quiz.data.model.VisitResponse
import com.besha.egyptguide.features.quiz.domain.remote.QuizRemoteClient
import com.besha.egyptguide.features.quiz.domain.repo.QuizRepo
import javax.inject.Inject

class QuizRepoImp @Inject constructor(private val quizRemoteClient: QuizRemoteClient) : QuizRepo {
    override suspend fun getQuiz(id: String): DataState<Quiz> {
        when (val result = quizRemoteClient.getQuiz(id)) {
            is DataState.Success -> {
                return DataState.Success(result.data)
            }
            else -> {
                return DataState.Error(Exception())
            }
        }
    }

    override suspend fun visit(visitRequest: VisitRequest): DataState<VisitResponse> {
        return quizRemoteClient.visit(visitRequest)
    }

    override suspend fun submitQuiz(submitQuizRequest: SubmitQuizRequest): DataState<SubmitQuizResponse> {
        return quizRemoteClient.submitQuiz(submitQuizRequest)
    }


}