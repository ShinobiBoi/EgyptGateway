package com.besha.egyptguide.features.tickets.domain.remote

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.tickets.data.dto.GetTicketResponse
import com.besha.egyptguide.features.tickets.data.dto.SubmitTicketRequest
import com.besha.egyptguide.features.tickets.data.dto.SubmitTicketResponse
import com.besha.egyptguide.features.tickets.data.dto.TicketDetailsResponse

interface TicketsRemoteClient {
    suspend fun submitTicket(submitTicketRequest: SubmitTicketRequest): DataState<SubmitTicketResponse>
    suspend fun getTickets(): DataState<GetTicketResponse>
    suspend fun getTicketDetails(ticketId: String): DataState<TicketDetailsResponse>
}
