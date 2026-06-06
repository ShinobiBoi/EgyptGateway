package com.besha.egyptguide.features.objectives.data.repo

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.objectives.data.dto.MonumentObjectivesResponse
import com.besha.egyptguide.features.objectives.data.dto.TicketObjectivesResponse
import com.besha.egyptguide.features.objectives.domain.remote.ObjectivesRemoteClient
import com.besha.egyptguide.features.objectives.domain.repo.ObjectivesRepo
import javax.inject.Inject

class ObjectivesRepoImp @Inject constructor(
    private val remoteClient: ObjectivesRemoteClient
) : ObjectivesRepo {
    override suspend fun getMonumentObjectives(): DataState<MonumentObjectivesResponse> {
        return remoteClient.getMonumentObjectives()
    }

    override suspend fun getTicketObjectives(): DataState<TicketObjectivesResponse> {
        return remoteClient.getTicketObjectives()
    }
}
