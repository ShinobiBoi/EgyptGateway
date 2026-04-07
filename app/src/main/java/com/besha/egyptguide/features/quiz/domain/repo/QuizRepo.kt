package com.besha.egyptguide.features.quiz.domain.repo

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.quiz.data.model.Quiz

interface QuizRepo {
    suspend fun getQuiz(id: String): DataState<Quiz>

}