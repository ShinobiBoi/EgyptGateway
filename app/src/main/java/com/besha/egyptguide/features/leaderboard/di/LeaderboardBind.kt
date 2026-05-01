package com.besha.egyptguide.features.leaderboard.di

import com.besha.egyptguide.features.leaderboard.data.remote.LeaderboardRemoteClientImp
import com.besha.egyptguide.features.leaderboard.data.repo.LeaderboardRepoImp
import com.besha.egyptguide.features.leaderboard.domain.remote.LeaderboardRemoteClient
import com.besha.egyptguide.features.leaderboard.domain.repo.LeaderboardRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LeaderboardBind {

    @Binds
    abstract fun bindLeaderboardRepo(leaderboardRepoImp: LeaderboardRepoImp): LeaderboardRepo

    @Binds
    abstract fun bindLeaderboardRemoteClient(leaderboardRemoteClientImp: LeaderboardRemoteClientImp): LeaderboardRemoteClient
}
