package com.besha.egyptguide.features.monuments.di

import com.besha.egyptguide.features.monuments.data.remote.MonumentRemoteClientImp
import com.besha.egyptguide.features.monuments.data.repo.MonumentRepoImp
import com.besha.egyptguide.features.monuments.domain.remote.MonumentRemoteClient
import com.besha.egyptguide.features.monuments.domain.repo.MonumentRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MonumentBind {

    @Binds
    abstract fun bindMonumentRepo(monumentRepoImp: MonumentRepoImp): MonumentRepo

    @Binds
    abstract fun bindMonumentRemoteClient(monumentRemoteClientImp: MonumentRemoteClientImp): MonumentRemoteClient
}
