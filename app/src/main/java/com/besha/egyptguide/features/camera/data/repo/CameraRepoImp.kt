package com.besha.egyptguide.features.camera.data.repo

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.data.remote.IdentifyResponse
import com.besha.egyptguide.features.camera.domain.repo.CameraRepo
import com.besha.egyptguide.features.camera.data.model.RatingRequest
import com.besha.egyptguide.features.camera.data.model.RatingResponse
import com.besha.egyptguide.features.camera.domain.remote.CameraRemote
import com.besha.egyptguide.features.camera.data.model.VisitRequest
import com.besha.egyptguide.features.camera.data.model.VisitResponse
import okhttp3.MultipartBody
import javax.inject.Inject

class CameraRepoImp @Inject constructor(private val cameraRemote: CameraRemote) : CameraRepo {
    override suspend fun identifyMonument(file: MultipartBody.Part): DataState<IdentifyResponse> {
        return cameraRemote.identifyMonument(file)
    }

    override suspend fun visit(visitRequest: VisitRequest): DataState<VisitResponse> {
        return cameraRemote.visit(visitRequest)
    }

    override suspend fun rate(ratingRequest: RatingRequest): DataState<RatingResponse> {
        return cameraRemote.rate(ratingRequest)
    }
}
