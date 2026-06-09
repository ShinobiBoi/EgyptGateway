package com.besha.egyptguide.features.objectives.presentaion.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.ViewState
import com.besha.egyptguide.features.objectives.data.dto.MonumentObjectivesDto
import com.besha.egyptguide.features.objectives.data.dto.TicketObjectivesDto

data class ObjectivesViewState(
    val monumentObjectives: CommonViewState<List<MonumentObjectivesDto>> = CommonViewState(),
    val ticketObjectives: CommonViewState<List<TicketObjectivesDto>> = CommonViewState()
) : ViewState
