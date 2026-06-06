package com.besha.egyptguide.features.objectives.domain.repo

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.objectives.data.dto.MonumentObjectivesResponse
import com.besha.egyptguide.features.objectives.data.dto.TicketObjectivesResponse

interface ObjectivesRepo {
    suspend fun getMonumentObjectives(): DataState<MonumentObjectivesResponse>
    suspend fun getTicketObjectives(): DataState<TicketObjectivesResponse>
}
