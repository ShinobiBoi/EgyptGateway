package com.besha.egyptguide.features.objectives.presentaion.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.Result
import com.besha.egyptguide.features.objectives.data.dto.MonumentObjectivesResponse
import com.besha.egyptguide.features.objectives.data.dto.TicketObjectivesResponse

sealed class ObjectivesResults : Result<ObjectivesViewState> {
    data class MonumentObjectivesResult(val result: CommonViewState<MonumentObjectivesResponse>) : ObjectivesResults() {
        override fun reduce(defaultState: ObjectivesViewState, oldState: ObjectivesViewState): ObjectivesViewState {
            return oldState.copy(monumentObjectives = result)
        }
    }

    data class TicketObjectivesResult(val result: CommonViewState<TicketObjectivesResponse>) : ObjectivesResults() {
        override fun reduce(defaultState: ObjectivesViewState, oldState: ObjectivesViewState): ObjectivesViewState {
            return oldState.copy(ticketObjectives = result)
        }
    }
}
