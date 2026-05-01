package com.besha.egyptguide.features.leaderboard.presenation.viewmodel

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.MVIBaseViewModel
import com.besha.egyptguide.features.leaderboard.domain.usecase.GetCurrentRankUseCase
import com.besha.egyptguide.features.leaderboard.domain.usecase.GetLeaderboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val getLeaderboardUseCase: GetLeaderboardUseCase,
    private val getCurrentRankUseCase: GetCurrentRankUseCase
) : MVIBaseViewModel<LeaderboardActions, LeaderboardResults, LeaderboardViewState>() {

    override val defaultViewState: LeaderboardViewState
        get() = LeaderboardViewState()

    override fun handleAction(action: LeaderboardActions): Flow<LeaderboardResults> = flow {
        when (action) {
            is LeaderboardActions.GetLeaderboard -> {
                handleGetLeadboard(this)
            }
            is LeaderboardActions.GetCurrentRank -> {
                handleGetCurrentRank(this)
            }
        }
    }

    private suspend fun FlowCollector<LeaderboardResults>.handleGetCurrentRank(collector: FlowCollector<LeaderboardResults>) {
        emit(LeaderboardResults.LoadingResult( true))

        when (val result = getCurrentRankUseCase()) {
            is DataState.Success -> {
                emit(LeaderboardResults.CurrentRankResult(CommonViewState(data = result.data)))
            }
            is DataState.Error -> {
                emit(LeaderboardResults.CurrentRankResult(CommonViewState(errorThrowable = result.throwable)))
            }
            else -> {}
        }

        emit(LeaderboardResults.LoadingResult( true))


    }

    private suspend fun FlowCollector<LeaderboardResults>.handleGetLeadboard(collector: FlowCollector<LeaderboardResults>) {
        emit(LeaderboardResults.LoadingResult( true))
        when (val result = getLeaderboardUseCase()) {
            is DataState.Success -> {
                emit(LeaderboardResults.LeaderboardResult(CommonViewState(data = result.data)))
            }
            is DataState.Error -> {
                emit(LeaderboardResults.LeaderboardResult(CommonViewState(errorThrowable = result.throwable)))
            }
            else -> {}
        }
        emit(LeaderboardResults.LoadingResult( false))

    }
}
