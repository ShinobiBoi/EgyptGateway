package com.besha.egyptguide.features.camera.data.remote

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.data.remote.BackEndServices
import com.besha.egyptguide.appcore.data.remote.IdentifyResponse
import com.besha.egyptguide.features.camera.data.model.RatingRequest
import com.besha.egyptguide.features.camera.data.model.RatingResponse
import com.besha.egyptguide.features.camera.domain.remote.CameraRemote
import com.besha.egyptguide.features.camera.domain.repo.CameraRepo
import okhttp3.MultipartBody
import javax.inject.Inject

class CameraRemoteImp @Inject constructor(private val backEndServices: BackEndServices) :
    CameraRemote {
    override suspend fun identifyMonument(file: MultipartBody.Part): DataState<IdentifyResponse> {
        return try {
            val response = backEndServices.identifyMonument(file)
            DataState.Success(response)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun rate(ratingRequest: RatingRequest): DataState<RatingResponse> {
        return try {
            val response = backEndServices.gamRating(
                id = ratingRequest.monument_id,
                score = ratingRequest.rating,
                crowdLevel = ratingRequest.crowd_level,
                comment = ratingRequest.comment
            )
            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            } else {
                DataState.Error(Exception("Rating failed"))
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}