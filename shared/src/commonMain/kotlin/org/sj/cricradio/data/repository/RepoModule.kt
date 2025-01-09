package org.sj.cricradio.data.repository

import org.koin.dsl.module


val repositoryModule = module {
    single<MatchRepository> { MatchRepoImpl(get(), get()) }
}