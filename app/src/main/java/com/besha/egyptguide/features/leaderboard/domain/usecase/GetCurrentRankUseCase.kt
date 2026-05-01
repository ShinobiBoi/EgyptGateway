package com.besha.egyptguide.features.leaderboard.domain.usecase

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardListItem
import com.besha.egyptguide.features.leaderboard.domain.repo.LeaderboardRepo
import javax.inject.Inject

class GetCurrentRankUseCase @Inject constructor(private val leaderboardRepo: LeaderboardRepo) {
    suspend operator fun invoke(): DataState<LeaderboardListItem> {
        return leaderboardRepo.getCurrentRank()
    }
}