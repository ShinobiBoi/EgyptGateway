package com.besha.egyptguide.features.tickets.domain.usecase

import com.besha.egyptguide.features.tickets.domain.repo.TicketsRepo
import okhttp3.MultipartBody
import javax.inject.Inject

class ScanTicketUseCase @Inject constructor(
    private val repo: TicketsRepo
) {
    suspend operator fun invoke(file: MultipartBody.Part) = repo.scanTicket(file)
}
