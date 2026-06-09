package com.besha.egyptguide.features.objectives.data.remote

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.data.remote.BackEndServices
import com.besha.egyptguide.features.objectives.data.dto.MonumentObjectivesDto
import com.besha.egyptguide.features.objectives.data.dto.TicketObjectivesDto
import com.besha.egyptguide.features.objectives.domain.remote.ObjectivesRemoteClient
import javax.inject.Inject

class ObjectivesRemoteClientImp @Inject constructor(
    private val backEndServices: BackEndServices
) : ObjectivesRemoteClient {
    override suspend fun getMonumentObjectives(): DataState<List<MonumentObjectivesDto>> {
        return try {
            val response = backEndServices.monumentObjectives()
            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            } else {
                DataState.Error(Throwable(response.message()))
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun getTicketObjectives(): DataState<List<TicketObjectivesDto>> {
        return try {
            val response = backEndServices.ticketObjectives()
            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            } else {
                DataState.Error(Throwable(response.message()))
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
