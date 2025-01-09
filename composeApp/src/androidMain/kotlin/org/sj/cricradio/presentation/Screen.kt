package org.sj.cricradio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import org.sj.cricradio.data.model.MiniMatchCardResponse
import org.sj.cricradio.data.model.VenueInfo
import org.sj.cricradio.data.model.VenueInfoResponse
import org.sj.cricradio.data.model.Weather


@Composable
fun MatchScreen(
    viewModel: MatchViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    when(uiState) {
        is MatchUiState.Loading -> LoadingScreen()
        is MatchUiState.Error -> ErrorScreen(
            message = (uiState as MatchUiState.Error).message,
            onRetry = viewModel::retryLoadingData
        )
        is MatchUiState.Success -> {
            val successState = uiState as MatchUiState.Success
            MatchContent(
                miniMatchCard = successState.miniMatchCard,
                venueInfo = successState.venueInfo,
                modifier = modifier
            )
        }
    }
}

@Composable
fun MatchContent(
    miniMatchCard: MiniMatchCardResponse?,
    venueInfo: VenueInfoResponse?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.Black)
    ) {
        // MiniScoreCard
        MiniScoreCard(miniMatchCard)

        // Venue Image
        VenueImage(venueInfo)

        //  Match Info
        MatchInfo(venueInfo)

        // Weather Info
        WeatherInfo(venueInfo?.responseData?.result?.weather)
    }
}

@Composable
fun MiniScoreCard(
    miniMatchCard: MiniMatchCardResponse?,
    modifier: Modifier = Modifier
) {
    val currentInning = miniMatchCard?.responseData?.result?.settingObj?.currentInning ?: 1
    val currentTeam = miniMatchCard?.responseData?.result?.settingObj?.currentTeam

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        contentColor = Color(0xFF0A1929)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Team A info
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = miniMatchCard?.responseData?.result?.teams?.a?.shortName ?: "",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                val teamAScore = if (currentInning == 2 && currentTeam == "a") {
                    miniMatchCard.responseData.result.teams.a.secondInningsScore
                } else {
                    miniMatchCard?.responseData?.result?.teams?.a?.firstInningsScore
                }
                Text(
                    text = "${teamAScore?.runs ?: 0}/${teamAScore?.wickets ?: 0}",
                    color = Color(0xFFFFB300),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = teamAScore?.overs ?: "0.0",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            // Runs needed
            Text(
                text = miniMatchCard?.responseData?.result?.now?.sessionLeft ?: "",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            // Team B info
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = miniMatchCard?.responseData?.result?.teams?.b?.shortName ?: "",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                val teamBScore = if (currentInning == 2 && currentTeam == "b") {
                    miniMatchCard.responseData.result.teams.b.bSecondInningsScore
                } else {
                    miniMatchCard?.responseData?.result?.teams?.b?.bFirstInningsScore
                }
                Text(
                    text = "${teamBScore?.runs ?: 0}/${teamBScore?.wickets ?: 0}",
                    color = Color(0xFFFFB300),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = teamBScore?.overs ?: "0.0",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun VenueImage(
    venueInfo: VenueInfoResponse?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        contentColor = Color(0xFF1E1E1E)
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            val imageUrl = venueInfo?.responseData?.result?.venueDetails?.photo

            if (imageUrl != null) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Venue Image",
                    contentScale = ContentScale.Crop,
                    modifier = modifier.fillMaxSize()
                ) {
                    when(painter.state) {
                        is AsyncImagePainter.State.Loading -> {
                            Box(
                                modifier = modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF0A1929)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF2196F3)
                                )
                            }
                        }
                        is AsyncImagePainter.State.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF0A1929)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Failed to load image",
                                    color = Color.Gray
                                )
                            }
                        }
                        else -> {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0A1929)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No venue image available",
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun MatchInfo(
    venueInfo: VenueInfoResponse?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = venueInfo?.responseData?.result?.venueDetails?.knownAs ?: "",
            color = Color(0xFF2196F3),
            fontSize = 16.sp
        )

        Text(
            text = venueInfo?.responseData?.result?.season?.name ?: "",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Text(
            text = venueInfo?.responseData?.result?.startDate?.str ?: "",
            color = Color.Gray,
            fontSize = 14.sp
        )

    }
}

@Composable
fun WeatherInfo(
    weather: Weather?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = Color(0xFF1E1E1E)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //TODO("Weather Icon would come here")
            Column(modifier = modifier.padding(start = 16.dp)) {
                Text(
                    text = "${weather?.tempC ?: 0}° C",
                    color = Color(0xFFFFB300),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = weather?.condition?.text ?: "",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = modifier.weight(1f))
            Text(
                text = "Last Updated: ${weather?.lastUpdated ?: ""}",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = Color.Red,
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}