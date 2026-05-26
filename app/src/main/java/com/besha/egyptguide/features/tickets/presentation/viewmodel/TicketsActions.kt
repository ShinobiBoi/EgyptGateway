package com.besha.egyptguide.features.tickets.presentation.viewmodel

import com.besha.egyptguide.appcore.mvi.Action
import com.besha.egyptguide.features.tickets.data.dto.SubmitTicketRequest
import com.google.android.libraries.places.api.model.AutocompleteSessionToken

sealed class TicketsActions : Action {
    object GetTickets : TicketsActions()
    
    data class GetTicketDetails(val ticketId: String) : TicketsActions()

    data class OnQueryChange(
        val query: String,
        val sessionToken: AutocompleteSessionToken?
    ) : TicketsActions()

    data class SelectPlace(
        val placeId: String,
        val sessionToken: AutocompleteSessionToken?
    ) : TicketsActions()

    data class SubmitTicket(val request: SubmitTicketRequest) : TicketsActions()
    
    object ResetSubmitState : TicketsActions()
}
