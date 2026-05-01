package com.besha.egyptguide.features.quiz.domain.usecase

import com.besha.egyptguide.features.quiz.data.model.VisitRequest
import com.besha.egyptguide.features.quiz.domain.repo.QuizRepo
import javax.inject.Inject

class VisitUseCase @Inject constructor(private val quizRepo: QuizRepo) {
    suspend operator fun invoke(visitRequest: VisitRequest) = quizRepo.visit(visitRequest)
}