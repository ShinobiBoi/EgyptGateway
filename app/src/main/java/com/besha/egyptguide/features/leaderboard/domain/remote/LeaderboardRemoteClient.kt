package com.besha.egyptguide.features.leaderboard.domain.remote

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardList
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardListItem

interface LeaderboardRemoteClient {
    suspend fun getLeaderboard(): DataState<LeaderboardList>

    suspend fun getCurrentRank(): DataState<LeaderboardListItem>

}
