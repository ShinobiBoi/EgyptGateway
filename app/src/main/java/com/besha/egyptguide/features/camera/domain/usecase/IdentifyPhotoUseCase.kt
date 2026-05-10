package com.besha.egyptguide.features.camera.domain.usecase

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.data.remote.IdentifyResponse
import com.besha.egyptguide.features.camera.domain.repo.CameraRepo
import okhttp3.MultipartBody
import javax.inject.Inject

class IdentifyPhotoUseCase @Inject constructor(private val cameraRepo: CameraRepo) {
    suspend operator fun invoke(file: MultipartBody.Part): DataState<IdentifyResponse> {
        return cameraRepo.identifyMonument(file)
    }
}
