package com.besha.egyptguide.features.leaderboard.domain.repo

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardList
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardListItem

interface LeaderboardRepo {
    suspend fun getLeaderboard(): DataState<LeaderboardList>
    suspend fun getCurrentRank(): DataState<LeaderboardListItem>
}
