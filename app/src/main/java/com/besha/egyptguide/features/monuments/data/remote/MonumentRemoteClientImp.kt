package com.besha.egyptguide.features.monuments.data.remote

import android.util.Log
import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.data.remote.BackEndServices
import com.besha.egyptguide.features.monuments.data.dto.MonumentDto
import com.besha.egyptguide.features.monuments.data.dto.RatingDto
import com.besha.egyptguide.features.monuments.data.dto.RatingSummaryDto
import com.besha.egyptguide.features.monuments.domain.remote.MonumentRemoteClient
import javax.inject.Inject

class MonumentRemoteClientImp @Inject constructor(
    private val backEndServices: BackEndServices
) : MonumentRemoteClient {
    override suspend fun getMonuments(): DataState<List<MonumentDto>> {
        return try {
            val response = backEndServices.getMonument()
            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            } else {
                DataState.Error(Throwable(response.message()))
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun getMonumentById(monumentId: String): DataState<MonumentDto> {
        return try {
            val response = backEndServices.getMonumentById(monumentId)
            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            } else {
                DataState.Error(Throwable(response.message()))
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun getMonumentRatings(monumentId: String, limit: Int): DataState<List<RatingDto>> {
        return try {
            val response = backEndServices.monumentRating(monumentId, limit)
            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            } else {
                DataState.Error(Throwable(response.message()))
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun getMonumentRatingSummary(monumentId: String): DataState<RatingSummaryDto> {
        return try {
            Log.d("MonumentViewModel","client: ${1}")

            val response = backEndServices.monumentRatingSummary(monumentId)

            Log.d("MonumentViewModel","client: ${response}")

            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            } else {
                Log.d("MonumentViewModel","client: ${response.message()}")

                DataState.Error(Throwable(response.message()))
            }
        } catch (e: Exception) {
            Log.d("MonumentViewModel","client exception: ${e.message}")
            DataState.Error(e)
        }
    }
}
