package com.besha.egyptguide.features.tickets.data.repo

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.tickets.data.dto.SubmitTicketRequest
import com.besha.egyptguide.features.tickets.data.dto.SubmitTicketResponse
import com.besha.egyptguide.features.tickets.data.dto.TicketDetailsResponse
import com.besha.egyptguide.features.tickets.data.mapper.toDomain
import com.besha.egyptguide.features.tickets.domain.models.Ticket
import com.besha.egyptguide.features.tickets.domain.models.TicketDetails
import com.besha.egyptguide.features.tickets.domain.remote.TicketsRemoteClient
import com.besha.egyptguide.features.tickets.domain.repo.TicketsRepo
import javax.inject.Inject

class TicketsRepoImp @Inject constructor(
    private val remoteClient: TicketsRemoteClient
) : TicketsRepo {

    override suspend fun getTickets(): DataState<List<Ticket>> {
        val result = remoteClient.getTickets()

        when (result) {
            is DataState.Success -> {
                return DataState.Success(result.data.map { it.toDomain() })
            }
            is DataState.Error<*> -> {
                return DataState.Error(result.throwable)
            }
            else -> {
                return DataState.Error(Throwable("Something went wrong"))
            }
        }
    }

    override suspend fun submitTicket(submitTicketRequest: SubmitTicketRequest): DataState<SubmitTicketResponse> {

        val result = remoteClient.submitTicket(submitTicketRequest)


        when (result) {
            is DataState.Success -> {
                if (result.data.success == true)
                    return DataState.Success(result.data)
                else {
                    return DataState.Error(Throwable(result.data.message))
                }
            }

            is DataState.Error -> {
                return DataState.Error(result.throwable)
            }

            else -> {
                return DataState.Error(Throwable("Something went wrong"))
            }


        }
    }

    override suspend fun getTicketDetails(ticketId: String): DataState<TicketDetails> {
       val result = remoteClient.getTicketDetails(ticketId)

        when (result) {
            is DataState.Success -> {
                return DataState.Success(result.data.toDomain() )
            }
            is DataState.Error<*> -> {
                return DataState.Error(result.throwable)
            }
            else -> {
                return DataState.Error(Throwable("Something went wrong"))
            }
        }
    }
}
