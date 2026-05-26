package com.besha.egyptguide.features.camera.data.remote

import android.util.Log
import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.data.remote.BackEndServices
import com.besha.egyptguide.appcore.data.remote.IdentifyResponse
import com.besha.egyptguide.features.camera.data.model.RatingRequest
import com.besha.egyptguide.features.camera.data.model.RatingResponse
import com.besha.egyptguide.features.camera.domain.remote.CameraRemote
import com.besha.egyptguide.features.camera.data.model.VisitRequest
import com.besha.egyptguide.features.camera.data.model.VisitResponse
import okhttp3.MultipartBody
import javax.inject.Inject

class CameraRemoteImp @Inject constructor(private val backEndServices: BackEndServices) :
    CameraRemote {
    override suspend fun identifyMonument(file: MultipartBody.Part): DataState<IdentifyResponse> {
        return try {
            val response = backEndServices.identifyMonument(file)

            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            } else {
                DataState.Error(Exception("Identify failed"))
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun visit(visitRequest: VisitRequest): DataState<VisitResponse> {
        return try {
            Log.d("CameraRemoteImp", "visit repo: $visitRequest")
            val response = backEndServices.gamVisit(visitRequest)
            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            }
            else{
                DataState.Error(Exception(response.errorBody()!!.string()))
            }
        }catch (e:Exception){
            DataState.Error(e)
        }
    }

    override suspend fun rate(ratingRequest: RatingRequest): DataState<RatingResponse> {
        return try {
            val response = backEndServices.gamRating(ratingRequest)
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