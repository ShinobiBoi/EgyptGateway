package com.besha.egyptguide.features.leaderboard.data.repo

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardList
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardListItem
import com.besha.egyptguide.features.leaderboard.domain.remote.LeaderboardRemoteClient
import com.besha.egyptguide.features.leaderboard.domain.repo.LeaderboardRepo
import javax.inject.Inject

class LeaderboardRepoImp @Inject constructor(private val leaderboardRemoteClient: LeaderboardRemoteClient) :
    LeaderboardRepo {
    override suspend fun getLeaderboard(): DataState<LeaderboardList> {
        return leaderboardRemoteClient.getLeaderboard()
    }

    override suspend fun getCurrentRank(): DataState<LeaderboardListItem> {
        return leaderboardRemoteClient.getCurrentRank()
    }
}
