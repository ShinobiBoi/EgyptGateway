package com.besha.egyptguide.features.objectives.di

import com.besha.egyptguide.features.objectives.data.remote.ObjectivesRemoteClientImp
import com.besha.egyptguide.features.objectives.data.repo.ObjectivesRepoImp
import com.besha.egyptguide.features.objectives.domain.remote.ObjectivesRemoteClient
import com.besha.egyptguide.features.objectives.domain.repo.ObjectivesRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ObjectivesBind {

    @Binds
    abstract fun bindObjectivesRepo(objectivesRepoImp: ObjectivesRepoImp): ObjectivesRepo

    @Binds
    abstract fun bindObjectivesRemoteClient(objectivesRemoteClientImp: ObjectivesRemoteClientImp): ObjectivesRemoteClient
}
