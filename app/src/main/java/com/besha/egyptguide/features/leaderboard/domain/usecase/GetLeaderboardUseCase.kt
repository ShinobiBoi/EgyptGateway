package com.besha.egyptguide.features.leaderboard.domain.usecase

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardList
import com.besha.egyptguide.features.leaderboard.domain.repo.LeaderboardRepo
import javax.inject.Inject

class GetLeaderboardUseCase @Inject constructor(private val leaderboardRepo: LeaderboardRepo) {
    suspend operator fun invoke(): DataState<LeaderboardList> {
        return leaderboardRepo.getLeaderboard()
    }
}
