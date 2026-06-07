package com.besha.egyptguide.features.camera.domain.usecase

import com.besha.egyptguide.features.camera.domain.repo.CameraRepo
import com.besha.egyptguide.features.camera.data.dto.RatingRequest
import javax.inject.Inject

class RateUseCase @Inject constructor(private val cameraRepo: CameraRepo) {
    suspend operator fun invoke(ratingRequest: RatingRequest) = cameraRepo.rate(ratingRequest)
}
