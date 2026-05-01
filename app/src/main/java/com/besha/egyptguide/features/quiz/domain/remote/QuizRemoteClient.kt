package com.besha.egyptguide.features.quiz.domain.remote

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.quiz.data.model.Quiz
import com.besha.egyptguide.features.quiz.data.model.RatingRequest
import com.besha.egyptguide.features.quiz.data.model.RatingResponse
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizRequest
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizResponse
import com.besha.egyptguide.features.quiz.data.model.VisitRequest
import com.besha.egyptguide.features.quiz.data.model.VisitResponse
import retrofit2.Response


interface QuizRemoteClient {

    suspend fun getQuiz(id: String): DataState<Quiz>
    suspend fun visit(visitRequest: VisitRequest): DataState<VisitResponse>
    suspend fun submitQuiz(submitQuizRequest: SubmitQuizRequest): DataState<SubmitQuizResponse>
    suspend fun rate(ratingRequest: RatingRequest): DataState<RatingResponse>

}