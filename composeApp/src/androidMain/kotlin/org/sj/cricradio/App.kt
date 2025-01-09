package org.sj.cricradio

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.sj.cricradio.presentation.MatchScreen
import org.sj.cricradio.presentation.MatchViewModel

@Composable
@Preview
fun App() {

    val viewModel: MatchViewModel = koinInject()

    MaterialTheme {
        MatchScreen(
            viewModel = viewModel
        )
    }
}