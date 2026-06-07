package com.besha.egyptguide.features.monuments.data.repo

import android.util.Log
import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.monuments.data.dto.MonumentDto
import com.besha.egyptguide.features.monuments.data.dto.RatingDto
import com.besha.egyptguide.features.monuments.data.dto.RatingSummaryDto
import com.besha.egyptguide.features.monuments.domain.remote.MonumentRemoteClient
import com.besha.egyptguide.features.monuments.domain.repo.MonumentRepo
import javax.inject.Inject

class MonumentRepoImp @Inject constructor(
    private val remoteClient: MonumentRemoteClient
) : MonumentRepo {
    override suspend fun getMonuments(): DataState<List<MonumentDto>> {
        return remoteClient.getMonuments()
    }

    override suspend fun getMonumentById(monumentId: String): DataState<MonumentDto> {
        return remoteClient.getMonumentById(monumentId)
    }

    override suspend fun getMonumentRatings(monumentId: String, limit: Int): DataState<List<RatingDto>> {
        return remoteClient.getMonumentRatings(monumentId, limit)
    }

    override suspend fun getMonumentRatingSummary(monumentId: String): DataState<RatingSummaryDto> {
        val response = remoteClient.getMonumentRatingSummary(monumentId)
        Log.d("MonumentViewModel",response.toString())
        return response
    }
}
