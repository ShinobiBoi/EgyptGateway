package com.besha.egyptguide.features.camera.domain.repo

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.data.remote.IdentifyResponse
import com.besha.egyptguide.features.camera.data.dto.RatingRequest
import com.besha.egyptguide.features.camera.data.dto.RatingResponse
import com.besha.egyptguide.features.camera.data.dto.VisitRequest
import com.besha.egyptguide.features.camera.data.dto.VisitResponse
import okhttp3.MultipartBody

interface CameraRepo {
    suspend fun identifyMonument(file: MultipartBody.Part): DataState<IdentifyResponse>

    suspend fun visit(visitRequest: VisitRequest): DataState<VisitResponse>

    suspend fun rate(ratingRequest: RatingRequest): DataState<RatingResponse>


}
