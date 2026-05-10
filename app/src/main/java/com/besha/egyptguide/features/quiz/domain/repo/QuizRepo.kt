package com.besha.egyptguide.features.quiz.domain.repo

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.quiz.data.model.Quiz
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizRequest
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizResponse
import com.besha.egyptguide.features.quiz.data.model.VisitRequest
import com.besha.egyptguide.features.quiz.data.model.VisitResponse

interface QuizRepo {
    suspend fun getQuiz(id: String): DataState<Quiz>

    suspend fun visit(visitRequest: VisitRequest): DataState<VisitResponse>
    suspend fun submitQuiz(submitQuizRequest: SubmitQuizRequest): DataState<SubmitQuizResponse>


}