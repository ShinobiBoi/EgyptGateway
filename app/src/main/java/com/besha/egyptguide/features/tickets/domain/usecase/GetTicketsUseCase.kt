package com.besha.egyptguide.features.tickets.domain.usecase

import com.besha.egyptguide.features.tickets.domain.repo.TicketsRepo
import javax.inject.Inject

class GetTicketsUseCase @Inject constructor(
    private val repo: TicketsRepo
) {
    suspend operator fun invoke() = repo.getTickets()
}
