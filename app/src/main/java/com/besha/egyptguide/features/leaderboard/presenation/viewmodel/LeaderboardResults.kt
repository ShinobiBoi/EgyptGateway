package com.besha.egyptguide.features.leaderboard.presenation.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.Result
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardList
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardListItem

sealed class LeaderboardResults : Result<LeaderboardViewState> {
    data class LeaderboardResult(val leaderboard: CommonViewState<LeaderboardList>) : LeaderboardResults() {
        override fun reduce(defaultState: LeaderboardViewState, oldState: LeaderboardViewState): LeaderboardViewState {
            return oldState.copy(leaderboard = leaderboard)
        }
    }

    data class CurrentRankResult(val currentRank: CommonViewState<LeaderboardListItem>) : LeaderboardResults() {
        override fun reduce(defaultState: LeaderboardViewState, oldState: LeaderboardViewState): LeaderboardViewState {
            return oldState.copy(currentRank = currentRank)
        }
    }

    data class LoadingResult(val isLoading: Boolean) : LeaderboardResults() {
        override fun reduce(defaultState: LeaderboardViewState, oldState: LeaderboardViewState): LeaderboardViewState {
            return oldState.copy(isLoading = isLoading)
        }
    }
}
