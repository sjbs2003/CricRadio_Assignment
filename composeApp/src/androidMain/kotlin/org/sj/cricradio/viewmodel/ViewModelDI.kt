package org.sj.cricradio.viewmodel

import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewmodelModule = module {
    viewModel {
        MatchViewModel(
            repository = get()
        )
    }
}