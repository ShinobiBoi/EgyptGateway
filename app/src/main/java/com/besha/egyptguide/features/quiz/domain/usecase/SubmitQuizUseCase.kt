package com.besha.egyptguide.features.quiz.domain.usecase

import com.besha.egyptguide.features.quiz.data.model.SubmitQuizRequest
import com.besha.egyptguide.features.quiz.domain.repo.QuizRepo
import javax.inject.Inject

class SubmitQuizUseCase @Inject constructor(private val quizRepo: QuizRepo) {
    suspend operator fun invoke(submitQuizRequest: SubmitQuizRequest) = quizRepo.submitQuiz(submitQuizRequest)

}