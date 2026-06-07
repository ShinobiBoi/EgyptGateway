package com.besha.egyptguide.features.monuments.presenation.viewmodel

import android.util.Log
import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.MVIBaseViewModel
import com.besha.egyptguide.features.monuments.domain.usecase.GetMonumentByIdUseCase
import com.besha.egyptguide.features.monuments.domain.usecase.GetMonumentRatingSummaryUseCase
import com.besha.egyptguide.features.monuments.domain.usecase.GetMonumentRatingsUseCase
import com.besha.egyptguide.features.monuments.domain.usecase.GetMonumentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class MonumentViewModel @Inject constructor(
    private val getMonumentsUseCase: GetMonumentsUseCase,
    private val getMonumentByIdUseCase: GetMonumentByIdUseCase,
    private val getMonumentRatingsUseCase: GetMonumentRatingsUseCase,
    private val getMonumentRatingSummaryUseCase: GetMonumentRatingSummaryUseCase
) : MVIBaseViewModel<MonumentActions, MonumentResults, MonumentViewState>() {

    override val defaultViewState: MonumentViewState
        get() = MonumentViewState()

    override fun handleAction(action: MonumentActions): Flow<MonumentResults> = flow {
        when (action) {
            is MonumentActions.GetMonuments -> {
                emit(MonumentResults.MonumentsResult(CommonViewState(isLoading = true)))
                when (val result = getMonumentsUseCase()) {
                    is DataState.Success -> {
                        emit(MonumentResults.MonumentsResult(CommonViewState(data = result.data)))
                    }
                    is DataState.Error -> {
                        emit(MonumentResults.MonumentsResult(CommonViewState(errorThrowable = result.throwable)))
                    }
                    else -> {}
                }
            }

            is MonumentActions.GetMonumentDetails -> {
                // Fetch monument details, rating summary, and initial ratings
                emit(MonumentResults.MonumentDetailsResult(CommonViewState(isLoading = true)))

                Log.d("MonumentViewModel", "Fetching monument details for ID: ${action.monumentId}")
                // Fetch Monument Info
                when (val result = getMonumentByIdUseCase(action.monumentId)) {
                    is DataState.Success -> {
                        emit(MonumentResults.MonumentDetailsResult(CommonViewState(data = result.data)))
                    }
                    is DataState.Error -> {
                        emit(MonumentResults.MonumentDetailsResult(CommonViewState(errorThrowable = result.throwable)))
                    }
                    else -> {}
                }

                // Fetch Summary
                when (val summaryResult = getMonumentRatingSummaryUseCase(action.monumentId)) {
                    is DataState.Success -> {
                        emit(MonumentResults.RatingSummaryResult(CommonViewState(data = summaryResult.data)))
                    }
                    else -> {
                        Log.e("MonumentViewModel", "Failed to fetch rating summary")
                    }
                }

                // Fetch initial 5 ratings
                fetchRatings(action.monumentId, 5, this)
            }

            is MonumentActions.GetMoreRatings -> {
                fetchRatings(action.monumentId, action.limit, this)
            }
        }
    }

    private suspend fun fetchRatings(monumentId: String, limit: Int, collector: kotlinx.coroutines.flow.FlowCollector<MonumentResults>) {
        collector.emit(MonumentResults.RatingsResult(CommonViewState(isLoading = true)))
        when (val result = getMonumentRatingsUseCase(monumentId, limit)) {
            is DataState.Success -> {
                collector.emit(MonumentResults.RatingsResult(CommonViewState(data = result.data)))
            }
            is DataState.Error -> {
                collector.emit(MonumentResults.RatingsResult(CommonViewState(errorThrowable = result.throwable)))
            }
            else -> {}
        }
    }
}
