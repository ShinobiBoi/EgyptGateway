package com.besha.egyptguide.features.tickets.presentation.viewmodel

import com.besha.egyptguide.appcore.data.remote.BackEndServices
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.MVIBaseViewModel
import com.besha.egyptguide.features.tickets.data.model.Ticket
import com.besha.egyptguide.features.tickets.data.model.TicketStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class TicketsViewModel @Inject constructor(
    private val backEndServices: BackEndServices
) : MVIBaseViewModel<TicketsActions, TicketsResults, TicketsViewState>() {

    override val defaultViewState: TicketsViewState
        get() = TicketsViewState()

    override fun handleAction(action: TicketsActions): Flow<TicketsResults> = flow {
        when (action) {
            is TicketsActions.GetTickets -> {
                emit(TicketsResults.GetTickets(CommonViewState(isLoading = true)))
                try {
                    // Mocking data as the API structure for list is not fully clear from BackEndServices
                    // In a real scenario, you'd call backEndServices.getTickets(...)
                    val mockTickets = listOf(
                        Ticket("1", "Giza Pyramids", TicketStatus.APPROVED, "2024-05-20"),
                        Ticket("2", "Luxor Temple", TicketStatus.PENDING, "2024-05-21"),
                        Ticket("3", "Karnak Temple", TicketStatus.DECLINED, "2024-05-22")
                    )
                    emit(TicketsResults.GetTickets(CommonViewState(data = mockTickets)))
                } catch (e: Exception) {
                    emit(TicketsResults.GetTickets(CommonViewState(errorThrowable = e)))
                }
            }
            is TicketsActions.ScanTicket -> {
                emit(TicketsResults.ScanTicket(CommonViewState(isLoading = true)))
                try {
                    val response = backEndServices.scanTicket(action.file)
                    emit(TicketsResults.ScanTicket(CommonViewState(data = response.isSuccessful)))
                    // Refresh list if scan was successful
                    if (response.isSuccessful) {
                        executeAction(TicketsActions.GetTickets)
                    }
                } catch (e: Exception) {
                    emit(TicketsResults.ScanTicket(CommonViewState(errorThrowable = e)))
                }
            }
        }
    }
}
