package com.besha.egyptguide.features.quiz.domain.remote

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.quiz.data.model.Quiz
import retrofit2.Response


interface QuizRemoteClient {

    suspend fun getQuiz(id: String): DataState<Quiz>
}