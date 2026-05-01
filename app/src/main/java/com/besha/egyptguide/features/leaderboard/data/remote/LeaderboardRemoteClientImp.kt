package com.besha.egyptguide.features.leaderboard.data.remote

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.data.remote.BackEndServices
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardList
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardListItem
import com.besha.egyptguide.features.leaderboard.domain.remote.LeaderboardRemoteClient
import javax.inject.Inject

class LeaderboardRemoteClientImp @Inject constructor(private val backEndServices: BackEndServices) :
    LeaderboardRemoteClient {


    override suspend fun getLeaderboard(): DataState<LeaderboardList> {
        return try {
            val response = backEndServices.getLeaderboard()
            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            } else {
                DataState.Error(Exception("Failed to fetch leaderboard"))
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun getCurrentRank(): DataState<LeaderboardListItem> {

        return DataState.Success(LeaderboardListItem(
            name = "besho",
            points = 50,
            rank = 36,
            user_id = ""
        ))


/*        return try {
            val response = backEndServices.getCurrentRank()
            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            } else {
                DataState.Error(Exception("Failed to fetch rank"))
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }*/


    }
}
