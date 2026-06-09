package com.besha.egyptguide.features.objectives.data.repo

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.objectives.data.dto.MonumentObjectivesDto
import com.besha.egyptguide.features.objectives.data.dto.TicketObjectivesDto
import com.besha.egyptguide.features.objectives.domain.remote.ObjectivesRemoteClient
import com.besha.egyptguide.features.objectives.domain.repo.ObjectivesRepo
import javax.inject.Inject

class ObjectivesRepoImp @Inject constructor(
    private val remoteClient: ObjectivesRemoteClient
) : ObjectivesRepo {
    override suspend fun getMonumentObjectives(): DataState<List<MonumentObjectivesDto>> {
        return remoteClient.getMonumentObjectives()
    }

    override suspend fun getTicketObjectives(): DataState<List<TicketObjectivesDto>> {
        return remoteClient.getTicketObjectives()
    }
}
