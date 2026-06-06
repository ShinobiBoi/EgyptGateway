package com.besha.egyptguide.features.objectives.presentaion.viewmodel

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.MVIBaseViewModel
import com.besha.egyptguide.features.objectives.domain.usecase.GetMonumentObjectivesUseCase
import com.besha.egyptguide.features.objectives.domain.usecase.GetTicketObjectivesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class ObjectivesViewModel @Inject constructor(
    private val getMonumentObjectivesUseCase: GetMonumentObjectivesUseCase,
    private val getTicketObjectivesUseCase: GetTicketObjectivesUseCase
) : MVIBaseViewModel<ObjectivesActions, ObjectivesResults, ObjectivesViewState>() {

    override val defaultViewState: ObjectivesViewState
        get() = ObjectivesViewState()

    override fun handleAction(action: ObjectivesActions): Flow<ObjectivesResults> = flow {
        when (action) {
            is ObjectivesActions.GetMonumentObjectives -> {
                emit(ObjectivesResults.MonumentObjectivesResult(CommonViewState(isLoading = true)))
                when (val result = getMonumentObjectivesUseCase()) {
                    is DataState.Success -> {
                        emit(ObjectivesResults.MonumentObjectivesResult(CommonViewState(data = result.data)))
                    }
                    is DataState.Error -> {
                        emit(ObjectivesResults.MonumentObjectivesResult(CommonViewState(errorThrowable = result.throwable)))
                    }
                    else -> {}
                }
            }
            is ObjectivesActions.GetTicketObjectives -> {
                emit(ObjectivesResults.TicketObjectivesResult(CommonViewState(isLoading = true)))
                when (val result = getTicketObjectivesUseCase()) {
                    is DataState.Success -> {
                        emit(ObjectivesResults.TicketObjectivesResult(CommonViewState(data = result.data)))
                    }
                    is DataState.Error -> {
                        emit(ObjectivesResults.TicketObjectivesResult(CommonViewState(errorThrowable = result.throwable)))
                    }
                    else -> {}
                }
            }
        }
    }
}
