package com.besha.egyptguide.features.tickets.di

import com.besha.egyptguide.features.tickets.data.remote.TicketsRemoteClientImp
import com.besha.egyptguide.features.tickets.data.repo.TicketsRepoImp
import com.besha.egyptguide.features.tickets.domain.remote.TicketsRemoteClient
import com.besha.egyptguide.features.tickets.domain.repo.TicketsRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TicketsBind {

    @Binds
    abstract fun bindTicketsRepo(ticketsRepoImp: TicketsRepoImp): TicketsRepo

    @Binds
    abstract fun bindTicketsRemoteClient(ticketsRemoteClientImp: TicketsRemoteClientImp): TicketsRemoteClient
}
