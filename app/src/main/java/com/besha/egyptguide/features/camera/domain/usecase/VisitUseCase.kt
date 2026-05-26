package com.besha.egyptguide.features.camera.domain.usecase

import com.besha.egyptguide.features.camera.domain.repo.CameraRepo
import com.besha.egyptguide.features.camera.data.model.VisitRequest
import javax.inject.Inject

class VisitUseCase @Inject constructor(private val cameraRepo: CameraRepo) {
    suspend operator fun invoke(visitRequest: VisitRequest) = cameraRepo.visit(visitRequest)
}