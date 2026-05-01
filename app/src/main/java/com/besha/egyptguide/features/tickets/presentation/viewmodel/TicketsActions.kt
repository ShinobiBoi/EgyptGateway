package com.besha.egyptguide.features.tickets.presentation.viewmodel

import com.besha.egyptguide.appcore.mvi.Action
import okhttp3.MultipartBody

sealed class TicketsActions : Action {
    object GetTickets : TicketsActions()
    data class ScanTicket(val file: MultipartBody.Part) : TicketsActions()
}
