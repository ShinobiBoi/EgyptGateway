package com.besha.egyptguide.features.quiz.domain.usecase

import com.besha.egyptguide.features.quiz.data.model.RatingRequest
import com.besha.egyptguide.features.quiz.domain.repo.QuizRepo
import javax.inject.Inject

class RateUseCase @Inject constructor(private val quizRepo: QuizRepo) {
    suspend operator fun invoke(ratingRequest: RatingRequest) = quizRepo.rate(ratingRequest)
}