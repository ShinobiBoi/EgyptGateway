package com.besha.egyptguide.features.tickets.presentation.viewmodel

import android.util.Log
import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.MVIBaseViewModel
import com.besha.egyptguide.features.maps.domain.usecases.QueryChangeUseCase
import com.besha.egyptguide.features.maps.domain.usecases.SetPlaceUseCase
import com.besha.egyptguide.features.tickets.domain.usecase.GetTicketDetailsUseCase
import com.besha.egyptguide.features.tickets.domain.usecase.GetTicketsUseCase
import com.besha.egyptguide.features.tickets.domain.usecase.SubmitTicketUseCase
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class TicketsViewModel @Inject constructor(
    private val submitTicketUseCase: SubmitTicketUseCase,
    private val getTicketsUseCase: GetTicketsUseCase,
    private val getTicketDetailsUseCase: GetTicketDetailsUseCase,
    private val queryChangeUseCase: QueryChangeUseCase,
    private val setPlaceUseCase: SetPlaceUseCase
) : MVIBaseViewModel<TicketsActions, TicketsResults, TicketsViewState>() {

    override val defaultViewState: TicketsViewState
        get() = TicketsViewState()

    override fun handleAction(action: TicketsActions): Flow<TicketsResults> = flow {
        when (action) {
            is TicketsActions.GetTickets -> {
                handleGetTickets(this)
            }

            is TicketsActions.GetTicketDetails -> {
                handleGetTicketDetails(action.ticketId, this)
            }

            is TicketsActions.OnQueryChange -> {
                handleQueryChange(action.query, action.sessionToken, this)
            }

            is TicketsActions.SelectPlace -> {
                handleSelectPlace(action.placeId, action.sessionToken, this)
            }

            is TicketsActions.SubmitTicket -> {
                emit(TicketsResults.SubmitTicket(CommonViewState(isLoading = true)))
                val result = submitTicketUseCase(action.request)
                when (result) {
                    is DataState.Success -> {
                        emit(TicketsResults.SubmitTicket(CommonViewState(data = result.data)))
                    }
                    is DataState.Error -> {
                        emit(TicketsResults.SubmitTicket(CommonViewState(errorThrowable = result.throwable)))
                    }
                    else -> {}
                }
            }

            is TicketsActions.ResetSubmitState -> {
                emit(TicketsResults.ResetSubmitState)
            }
        }
    }

    private suspend fun handleGetTickets(collector: FlowCollector<TicketsResults>) {
        collector.emit(TicketsResults.GetTickets(CommonViewState(isLoading = true)))
        when(val result = getTicketsUseCase()){
            is DataState.Success -> {
                collector.emit(TicketsResults.GetTickets(CommonViewState(data = result.data)))
            }
            is DataState.Error -> {
                collector.emit(TicketsResults.GetTickets(CommonViewState(errorThrowable = result.throwable)))
            }
            else -> {}
        }
    }

    private suspend fun handleGetTicketDetails(ticketId: String, collector: FlowCollector<TicketsResults>) {
        collector.emit(TicketsResults.TicketDetails(CommonViewState(isLoading = true)))
        when(val result = getTicketDetailsUseCase(ticketId)) {
            is DataState.Success -> {
                collector.emit(TicketsResults.TicketDetails(CommonViewState(data = result.data)))
            }
            is DataState.Error -> {
                collector.emit(TicketsResults.TicketDetails(CommonViewState(errorThrowable = result.throwable)))
            }
            else -> {}
        }
    }

    private suspend fun handleQueryChange(
        newQuery: String,
        sessionToken: AutocompleteSessionToken?,
        collector: FlowCollector<TicketsResults>
    ) {
        collector.emit(TicketsResults.OnQueryChange(newQuery))
        val token = sessionToken ?: AutocompleteSessionToken.newInstance().also {
            collector.emit(TicketsResults.refreshToken(it))
        }
        val predictions = queryChangeUseCase(newQuery, token)
        collector.emit(TicketsResults.Predictions(CommonViewState(data = predictions)))
    }

    private suspend fun handleSelectPlace(
        placeId: String,
        sessionToken: AutocompleteSessionToken?,
        collector: FlowCollector<TicketsResults>
    ) {
        val token = sessionToken ?: AutocompleteSessionToken.newInstance().also {
            collector.emit(TicketsResults.refreshToken(it))
        }
        val place = setPlaceUseCase(placeId, token)
        collector.emit(TicketsResults.SelectedPlace(CommonViewState(data = place)))
        collector.emit(TicketsResults.refreshToken(null))
    }
}
