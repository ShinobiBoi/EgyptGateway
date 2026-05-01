package com.besha.egyptguide.features.tickets.presentation.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.ViewState
import com.besha.egyptguide.features.tickets.data.model.Ticket

data class TicketsViewState(
    val tickets: CommonViewState<List<Ticket>> = CommonViewState(),
    val scanResult: CommonViewState<Boolean> = CommonViewState()
) : ViewState
