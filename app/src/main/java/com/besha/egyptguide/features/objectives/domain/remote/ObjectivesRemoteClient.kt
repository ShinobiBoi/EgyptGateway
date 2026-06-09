package com.besha.egyptguide.features.objectives.domain.remote

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.objectives.data.dto.MonumentObjectivesDto
import com.besha.egyptguide.features.objectives.data.dto.TicketObjectivesDto

interface ObjectivesRemoteClient {
    suspend fun getMonumentObjectives(): DataState<List<MonumentObjectivesDto>>
    suspend fun getTicketObjectives(): DataState<List<TicketObjectivesDto>>
}
