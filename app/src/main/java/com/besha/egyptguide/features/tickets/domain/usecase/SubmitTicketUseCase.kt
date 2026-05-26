package com.besha.egyptguide.features.tickets.domain.usecase

import com.besha.egyptguide.features.tickets.data.dto.SubmitTicketRequest
import com.besha.egyptguide.features.tickets.domain.repo.TicketsRepo
import javax.inject.Inject

class SubmitTicketUseCase @Inject constructor(private val ticketsRepo: TicketsRepo) {
    suspend operator fun invoke(submitTicketRequest: SubmitTicketRequest) =
        ticketsRepo.submitTicket(submitTicketRequest)
}
