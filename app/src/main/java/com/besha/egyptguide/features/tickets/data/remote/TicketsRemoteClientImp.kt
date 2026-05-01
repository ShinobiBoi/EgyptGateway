package com.besha.egyptguide.features.tickets.data.remote

import com.besha.egyptguide.appcore.data.remote.BackEndServices
import com.besha.egyptguide.appcore.data.remote.ScanResponse
import com.besha.egyptguide.features.tickets.domain.remote.TicketsRemoteClient
import okhttp3.MultipartBody
import javax.inject.Inject

class TicketsRemoteClientImp @Inject constructor(
    private val backEndServices: BackEndServices
) : TicketsRemoteClient {
    override suspend fun scanTicket(file: MultipartBody.Part): ScanResponse {
        return backEndServices.scanTicket(file)
    }
}
