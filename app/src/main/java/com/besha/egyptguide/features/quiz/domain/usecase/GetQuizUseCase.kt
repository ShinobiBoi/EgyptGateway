package com.besha.egyptguide.features.quiz.domain.usecase

import com.besha.egyptguide.features.quiz.domain.repo.QuizRepo
import javax.inject.Inject

class GetQuizUseCase @Inject constructor(private val quizRepo: QuizRepo) {
    suspend operator fun invoke(id: String) = quizRepo.getQuiz(id)

}