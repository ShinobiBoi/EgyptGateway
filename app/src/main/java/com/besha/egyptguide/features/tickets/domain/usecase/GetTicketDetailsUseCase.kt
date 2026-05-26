package com.besha.egyptguide.features.tickets.domain.usecase

import com.besha.egyptguide.features.tickets.domain.repo.TicketsRepo
import javax.inject.Inject

class GetTicketDetailsUseCase @Inject constructor(private val ticketsRepo: TicketsRepo) {
    suspend operator fun invoke(ticketId: String) = ticketsRepo.getTicketDetails(ticketId)
}
