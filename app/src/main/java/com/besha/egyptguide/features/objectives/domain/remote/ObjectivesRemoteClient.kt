package com.besha.egyptguide.features.objectives.domain.remote

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.objectives.data.dto.MonumentObjectivesResponse
import com.besha.egyptguide.features.objectives.data.dto.TicketObjectivesResponse

interface ObjectivesRemoteClient {
    suspend fun getMonumentObjectives(): DataState<MonumentObjectivesResponse>
    suspend fun getTicketObjectives(): DataState<TicketObjectivesResponse>
}
