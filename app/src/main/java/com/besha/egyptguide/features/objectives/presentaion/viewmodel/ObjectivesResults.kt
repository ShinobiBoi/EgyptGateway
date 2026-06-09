package com.besha.egyptguide.features.objectives.presentaion.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.Result
import com.besha.egyptguide.features.objectives.data.dto.MonumentObjectivesDto
import com.besha.egyptguide.features.objectives.data.dto.TicketObjectivesDto

sealed class ObjectivesResults : Result<ObjectivesViewState> {
    data class MonumentObjectivesResult(val result: CommonViewState<List<MonumentObjectivesDto>>) : ObjectivesResults() {
        override fun reduce(defaultState: ObjectivesViewState, oldState: ObjectivesViewState): ObjectivesViewState {
            return oldState.copy(monumentObjectives = result)
        }
    }

    data class TicketObjectivesResult(val result: CommonViewState<List<TicketObjectivesDto>>) : ObjectivesResults() {
        override fun reduce(defaultState: ObjectivesViewState, oldState: ObjectivesViewState): ObjectivesViewState {
            return oldState.copy(ticketObjectives = result)
        }
    }
}
