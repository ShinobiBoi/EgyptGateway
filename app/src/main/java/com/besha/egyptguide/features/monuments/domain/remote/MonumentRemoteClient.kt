package com.besha.egyptguide.features.monuments.domain.remote

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.monuments.data.dto.MonumentDto
import com.besha.egyptguide.features.monuments.data.dto.RatingDto
import com.besha.egyptguide.features.monuments.data.dto.RatingSummaryDto
import com.besha.egyptguide.features.monuments.domain.model.Rating

interface MonumentRemoteClient {
    suspend fun getMonuments(): DataState<List<MonumentDto>>
    suspend fun getMonumentById(monumentId: String): DataState<MonumentDto>
    suspend fun getMonumentRatings(monumentId: String, limit: Int): DataState<List<Rating>>
    suspend fun getMonumentRatingSummary(monumentId: String): DataState<RatingSummaryDto>
}
