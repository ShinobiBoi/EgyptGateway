package com.besha.egyptguide.features.camera.domain.remote

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.data.remote.IdentifyResponse
import com.besha.egyptguide.features.camera.data.model.RatingRequest
import com.besha.egyptguide.features.camera.data.model.RatingResponse
import okhttp3.MultipartBody

interface CameraRemote {
    suspend fun identifyMonument(file: MultipartBody.Part): DataState<IdentifyResponse>
    suspend fun rate(ratingRequest: RatingRequest): DataState<RatingResponse>
}