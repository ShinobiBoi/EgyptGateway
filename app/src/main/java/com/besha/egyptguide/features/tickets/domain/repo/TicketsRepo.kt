package com.besha.egyptguide.features.tickets.domain.repo

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.tickets.data.dto.SubmitTicketRequest
import com.besha.egyptguide.features.tickets.data.dto.SubmitTicketResponse
import com.besha.egyptguide.features.tickets.domain.models.Ticket
import com.besha.egyptguide.features.tickets.domain.models.TicketDetails

interface TicketsRepo {
    suspend fun getTickets(): DataState<List<Ticket>>
    suspend fun submitTicket(submitTicketRequest: SubmitTicketRequest): DataState<SubmitTicketResponse>
    suspend fun getTicketDetails(ticketId: String): DataState<TicketDetails>
}
