package com.besha.egyptguide.features.tickets.presentation.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.Result
import com.besha.egyptguide.features.tickets.data.model.Ticket

sealed class TicketsResults : Result<TicketsViewState> {
    data class GetTickets(val tickets: CommonViewState<List<Ticket>>) : TicketsResults() {
        override fun reduce(defaultState: TicketsViewState, oldState: TicketsViewState): TicketsViewState {
            return oldState.copy(tickets = tickets)
        }
    }

    data class ScanTicket(val scanResult: CommonViewState<Boolean>) : TicketsResults() {
        override fun reduce(defaultState: TicketsViewState, oldState: TicketsViewState): TicketsViewState {
            return oldState.copy(scanResult = scanResult)
        }
    }
}
