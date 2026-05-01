package com.besha.egyptguide.features.leaderboard.presenation.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.ViewState
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardList
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardListItem

data class LeaderboardViewState(
    val leaderboard: CommonViewState<LeaderboardList> = CommonViewState(),
    val currentRank: CommonViewState<LeaderboardListItem> = CommonViewState(),
    val isLoading: Boolean = false
) : ViewState
