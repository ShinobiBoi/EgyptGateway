package com.besha.egyptguide.features.tickets.presentation.viewmodel

import com.besha.egyptguide.appcore.data.model.MyPlace
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.Result
import com.besha.egyptguide.features.tickets.data.dto.SubmitTicketResponse
import com.besha.egyptguide.features.tickets.domain.models.Ticket
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken

sealed class TicketsResults : Result<TicketsViewState> {
    data class GetTickets(val tickets: CommonViewState<List<Ticket>>) : TicketsResults() {
        override fun reduce(defaultState: TicketsViewState, oldState: TicketsViewState): TicketsViewState {
            return oldState.copy(tickets = tickets)
        }
    }

    data class TicketDetails(val ticketDetails: CommonViewState<com.besha.egyptguide.features.tickets.domain.models.TicketDetails>) : TicketsResults() {
        override fun reduce(defaultState: TicketsViewState, oldState: TicketsViewState): TicketsViewState {
            return oldState.copy(ticketDetails = ticketDetails)
        }
    }

    data class OnQueryChange(val query: String) : TicketsResults() {
        override fun reduce(defaultState: TicketsViewState, oldState: TicketsViewState): TicketsViewState {
            return oldState.copy(query = query)
        }
    }

    data class Predictions(val predictions: CommonViewState<List<AutocompletePrediction>>) :
        TicketsResults() {
        override fun reduce(
            defaultState: TicketsViewState,
            oldState: TicketsViewState
        ): TicketsViewState {
            return oldState.copy(predictions = predictions)
        }
    }

    data class SelectedPlace(val selectedPlace: CommonViewState<MyPlace>) : TicketsResults() {
        override fun reduce(defaultState: TicketsViewState, oldState: TicketsViewState): TicketsViewState {
            return oldState.copy(selectedPlace = selectedPlace)
        }
    }

    data class refreshToken(val newToken: AutocompleteSessionToken?) : TicketsResults() {
        override fun reduce(
            defaultState: TicketsViewState,
            oldState: TicketsViewState
        ): TicketsViewState {
            return oldState.copy(sessionToken = newToken)
        }
    }

    data class SubmitTicket(val submitResult: CommonViewState<SubmitTicketResponse>) : TicketsResults() {
        override fun reduce(defaultState: TicketsViewState, oldState: TicketsViewState): TicketsViewState {
            return oldState.copy(submitResult = submitResult)
        }
    }

    object ResetSubmitState : TicketsResults() {
        override fun reduce(defaultState: TicketsViewState, oldState: TicketsViewState): TicketsViewState {
            return oldState.copy(submitResult = CommonViewState(), selectedPlace = CommonViewState(), query = "")
        }
    }
}
