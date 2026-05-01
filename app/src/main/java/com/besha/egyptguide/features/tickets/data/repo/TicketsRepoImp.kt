package com.besha.egyptguide.features.tickets.data.repo

import com.besha.egyptguide.appcore.data.remote.ScanResponse
import com.besha.egyptguide.features.tickets.data.model.Ticket
import com.besha.egyptguide.features.tickets.data.model.TicketStatus
import com.besha.egyptguide.features.tickets.domain.remote.TicketsRemoteClient
import com.besha.egyptguide.features.tickets.domain.repo.TicketsRepo
import okhttp3.MultipartBody
import javax.inject.Inject

class TicketsRepoImp @Inject constructor(
    private val remoteClient: TicketsRemoteClient
) : TicketsRepo {
    override suspend fun getTickets(): List<Ticket> {
        // Mocking data as per current project state
        return listOf(
            Ticket("1", "Giza Pyramids", TicketStatus.APPROVED, "2024-05-20"),
            Ticket("2", "Luxor Temple", TicketStatus.PENDING, "2024-05-21"),
            Ticket("3", "Karnak Temple", TicketStatus.DECLINED, "2024-05-22")
        )
    }

    override suspend fun scanTicket(file: MultipartBody.Part): ScanResponse {
        return remoteClient.scanTicket(file)
    }
}
