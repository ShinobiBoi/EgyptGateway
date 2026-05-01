package com.besha.egyptguide.features.tickets.domain.repo

import com.besha.egyptguide.features.tickets.data.model.Ticket
import com.besha.egyptguide.appcore.data.remote.ScanResponse
import okhttp3.MultipartBody

interface TicketsRepo {
    suspend fun getTickets(): List<Ticket>
    suspend fun scanTicket(file: MultipartBody.Part): ScanResponse
}
