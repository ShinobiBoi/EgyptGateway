package com.besha.egyptguide.features.tickets.domain.remote

import com.besha.egyptguide.appcore.data.remote.ScanResponse
import okhttp3.MultipartBody

interface TicketsRemoteClient {
    suspend fun scanTicket(file: MultipartBody.Part): ScanResponse
}
