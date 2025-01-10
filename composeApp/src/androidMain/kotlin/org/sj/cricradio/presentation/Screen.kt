package org.sj.cricradio.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import org.sj.cricradio.R
import org.sj.cricradio.data.model.MiniMatchCardResponse
import org.sj.cricradio.data.model.VenueInfoResponse
import org.sj.cricradio.data.model.VenueStats
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

        // Toss Info
        TossDetails(venueInfo)

        // Umpire Details
        UmpireDetails(venueInfo)

        // Weather Info
        WeatherInfo(venueInfo?.responseData?.result?.weather)

        // Venue Stats
        VenueStats(venueInfo?.responseData?.result?.venueStats)
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
        backgroundColor = Color(0xFF0A1929)
    ) {
        Column {
        //  Main Score Section
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team A info
                Box(modifier = Modifier.weight(0.3f)) {
                    TeamScore(
                        teamShortName = miniMatchCard?.responseData?.result?.teams?.a?.shortName
                            ?: "",
                        isTeamA = true,
                        currentTeam = currentTeam,
                        score = if (currentInning == 2 && currentTeam == "a") {
                            miniMatchCard.responseData.result.teams.a.secondInningsScore
                        } else {
                            miniMatchCard?.responseData?.result?.teams?.a?.firstInningsScore
                        }
                    )
                }

                // Last Commentary Primary Text
                Box(
                    modifier = Modifier.weight(0.4f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = miniMatchCard?.responseData?.result?.lastCommentary?.primaryText ?: "",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                // Team B info
                Box(
                    modifier = Modifier.weight(0.3f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TeamScore(
                        teamShortName = miniMatchCard?.responseData?.result?.teams?.b?.shortName
                            ?: "",
                        isTeamA = false,
                        currentTeam = currentTeam,
                        score = if (currentInning == 2 && currentTeam == "b") {
                            miniMatchCard.responseData.result.teams.b.bSecondInningsScore
                        } else {
                            miniMatchCard?.responseData?.result?.teams?.b?.bFirstInningsScore
                        }
                    )
                }
            }

            Divider(
                color = Color(0xFF1E3A5F),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Bottom section with CRR and announcement
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CRR: ${miniMatchCard?.responseData?.result?.now?.runRate ?: "0.00"}",
                    color = Color(0xFF64B5F6),
                    fontSize = 14.sp
                )
                Text(
                    text = miniMatchCard?.responseData?.result?.announcement1 ?: "",
                    color = Color(0xFF64B5F6),
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
fun TossDetails(
    venueInfo: VenueInfoResponse?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color(0xFF1E1E1E)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = venueInfo?.responseData?.result?.toss?.str ?: "",
                color = Color(0xFFFFB300),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun UmpireDetails(
    venueInfo: VenueInfoResponse?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Title outside the card
        Text(
            text = "Umpires",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            backgroundColor = Color(0xFF1A1A1A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // First row with two umpires
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // First Umpire
                    Column {
                        Text(
                            text = "Umpire",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = venueInfo?.responseData?.result?.firstUmpire ?: "",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Second Umpire
                    Column {
                        Text(
                            text = "Umpire",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = venueInfo?.responseData?.result?.secoundUmpire ?: "",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Divider
                Divider(
                    color = Color(0xFF2A2A2A),
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )

                // Second row with Third Umpire and Referee
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Third/TV Umpire
                    Column {
                        Text(
                            text = "Third/TV Umpire",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = venueInfo?.responseData?.result?.thirdUmpire ?: "",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Referee
                    Column {
                        Text(
                            text = "Referee",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = venueInfo?.responseData?.result?.matchReferee ?: "",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherInfo(
    weather: Weather?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Weather",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            backgroundColor = Color(0xFF1A1A1A)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left section with image and location
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.overcast),
                        contentDescription = "Weather icon",
                        modifier = Modifier.size(40.dp)
                    )

                    Column(
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        Text(
                            text = weather?.location ?: "Geeberha",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "${weather?.tempC?.toInt() ?: 20}° C",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = weather?.condition?.text ?: "Overcast",
                            color = Color(0xFF2196F3),
                            fontSize = 14.sp
                        )
                    }
                }

                // Vertical Divider
                Divider(
                    color = Color.Yellow,
                    modifier = Modifier
                        .width(1.dp)
                        .height(48.dp)
                        .padding(horizontal = 16.dp)
                )

                // Right section with last updated info
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Last Updated",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = weather?.lastUpdated?.split(" ")?.getOrNull(1) ?: "15:00",
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun VenueStats(
    stats: VenueStats?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color(0xFF1E1E1E)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Venue Stats",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = modifier.padding(bottom = 16.dp)
            )

            // General Stats with Dividers
            VenueStatItem("Matches Played", stats?.matchesPlayed?.toString() ?: "0")
            Divider(
                color = Color(0xFF2A2A2A),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            VenueStatItem("Lowest Defended", stats?.lowestDefended?.toString() ?: "0")
            Divider(
                color = Color(0xFF2A2A2A),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            VenueStatItem("Highest Chased", stats?.highestChased?.toString() ?: "0")
            Divider(
                color = Color(0xFF2A2A2A),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            VenueStatItem("Won Bat First", stats?.batFirstWins?.toString() ?: "0")
            Divider(
                color = Color(0xFF2A2A2A),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            VenueStatItem("Won Ball First", stats?.ballFirstWins?.toString() ?: "0")
            Divider(
                color = Color(0xFF2A2A2A),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Innings Stats Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // First Innings Column
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "1st Inn",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    InningsStatItem("Avg Score", stats?.battingFirst?.averageScore?.toString() ?: "0")
                    InningsStatItem("Highest Score", stats?.battingFirst?.highestScore?.toString() ?: "0")
                    InningsStatItem("Lowest Score", stats?.battingFirst?.lowestScore?.toString() ?: "0")
                    InningsStatItem(
                        "Pace Wickets",
                        "${stats?.battingFirst?.paceWickets ?: 0} (${calculatePercentage(
                            stats?.battingFirst?.paceWickets,
                            stats?.battingFirst?.paceWickets?.plus(stats.battingFirst.spinWickets)
                        )}%)"
                    )
                    InningsStatItem(
                        "Spin Wickets",
                        "${stats?.battingFirst?.spinWickets ?: 0} (${calculatePercentage(
                            stats?.battingFirst?.spinWickets,
                            stats?.battingFirst?.spinWickets?.plus(stats.battingFirst.paceWickets)
                        )}%)"
                    )
                }

                // Vertical Divider
                Divider(
                    color = Color(0xFF2A2A2A),
                    modifier = Modifier
                        .width(1.dp)
                        .height(150.dp)
                        .padding(horizontal = 8.dp)
                )

                // Second Innings Column
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "2nd Inn",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    InningsStatItem("Avg Score", stats?.battingSecond?.averageScore?.toString() ?: "0")
                    InningsStatItem("Highest Score", stats?.battingSecond?.highestScore?.toString() ?: "0")
                    InningsStatItem("Lowest Score", stats?.battingSecond?.lowestScore?.toString() ?: "0")
                    InningsStatItem(
                        "Pace Wickets",
                        "${stats?.battingSecond?.paceWickets ?: 0} (${calculatePercentage(
                            stats?.battingSecond?.paceWickets,
                            stats?.battingSecond?.paceWickets?.plus(stats.battingSecond.spinWickets)
                        )}%)"
                    )
                    InningsStatItem(
                        "Spin Wickets",
                        "${stats?.battingSecond?.spinWickets ?: 0} (${calculatePercentage(
                            stats?.battingSecond?.spinWickets,
                            stats?.battingSecond?.spinWickets?.plus(stats.battingSecond.paceWickets)
                        )}%)"
                    )
                }
            }
        }
    }
}