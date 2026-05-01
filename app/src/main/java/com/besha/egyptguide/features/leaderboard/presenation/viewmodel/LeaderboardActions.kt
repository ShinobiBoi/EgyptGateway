package com.besha.egyptguide.features.leaderboard.presenation.viewmodel

import com.besha.egyptguide.appcore.mvi.Action

sealed class LeaderboardActions : Action {
    object GetLeaderboard : LeaderboardActions()
    object GetCurrentRank : LeaderboardActions()

}
