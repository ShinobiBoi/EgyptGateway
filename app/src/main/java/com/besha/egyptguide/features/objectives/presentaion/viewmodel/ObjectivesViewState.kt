package com.besha.egyptguide.features.objectives.presentaion.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.ViewState
import com.besha.egyptguide.features.objectives.data.dto.MonumentObjectivesResponse
import com.besha.egyptguide.features.objectives.data.dto.TicketObjectivesResponse

data class ObjectivesViewState(
    val monumentObjectives: CommonViewState<MonumentObjectivesResponse> = CommonViewState(),
    val ticketObjectives: CommonViewState<TicketObjectivesResponse> = CommonViewState()
) : ViewState
