package com.besha.egyptguide.features.tickets.presentation.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.ViewState
import com.besha.egyptguide.features.tickets.data.dto.TicketDetailsResponse
import com.besha.egyptguide.features.tickets.domain.models.Ticket
import com.besha.egyptguide.features.tickets.data.dto.SubmitTicketResponse
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.besha.egyptguide.appcore.data.model.MyPlace
import com.besha.egyptguide.features.tickets.domain.models.TicketDetails

data class TicketsViewState(
    val tickets: CommonViewState<List<Ticket>> = CommonViewState(),
    val ticketDetails: CommonViewState<TicketDetails> = CommonViewState(),
    val submitResult: CommonViewState<SubmitTicketResponse> = CommonViewState(),
    val predictions: CommonViewState<List<AutocompletePrediction>> = CommonViewState(),
    val selectedPlace: CommonViewState<MyPlace> = CommonViewState(),
    val query: String = "",
    val sessionToken: AutocompleteSessionToken? = AutocompleteSessionToken.newInstance()
) : ViewState
