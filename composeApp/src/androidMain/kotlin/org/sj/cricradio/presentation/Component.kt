package org.sj.cricradio.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sj.cricradio.R
import org.sj.cricradio.data.model.InningsScore

@Composable
fun TeamScore(
    modifier: Modifier = Modifier,
    teamShortName: String,
    isTeamA: Boolean,
    currentTeam: String? = null,
    score: InningsScore?
) {
    Column(
        horizontalAlignment = if (isTeamA) Alignment.Start else Alignment.End,
        modifier = modifier.width(IntrinsicSize.Max)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isTeamA) Arrangement.Start else Arrangement.End,
            modifier = modifier.fillMaxWidth()
        ) {
            if (isTeamA) {
                TeamFlag(teamShortName = teamShortName)
                if (currentTeam == "a") {
                    Spacer(modifier = Modifier.width(4.dp))
                    BatIcon()
                }
            } else {
                if (currentTeam == "b") {  // Only show bat icon if this team is batting
                    BatIcon()
                    Spacer(modifier = Modifier.width(4.dp))
                }
                TeamFlag(teamShortName = teamShortName)
            }
        }

        Text(
            text = "${score?.runs ?: 0}/${score?.wickets ?: 0}",
            color = Color(0xFFFFB300),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = score?.overs ?: "0.0",
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

@Composable
fun TeamFlag(
    teamShortName: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(
                id = if (teamShortName == "SA") {
                    R.drawable.sa_flag
                } else  R.drawable.sl_flag
            ),
            contentDescription = "$teamShortName flag",
            modifier = modifier.size(20.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = teamShortName,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun BatIcon() {
    Image(
        painter = painterResource(id = R.drawable.bat),
        contentDescription = "Batting indicator",
        modifier = Modifier.size(16.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun BattingStatsComparison(
    label: String,
    firstInnings: String,
    secondInnings: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = firstInnings,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Normal
        )
        Text(
            text = secondInnings,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Normal
        )
    }
}

fun calculatePercentage(part: Int?, total: Int?): Int {
    if (part == null || total == null || total == 0) return 0
    return ((part.toFloat() / total.toFloat()) * 100).toInt()
}

@Composable
fun VenueStatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun InningsStatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
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


@Composable
fun WebSocketTestSection(
    isConnected: Boolean,
    message: String,
    onMessageSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputMessage by remember { mutableStateOf("") }

    LaunchedEffect(isConnected) {
        println("WebSocket connection state: $isConnected")
    }

    LaunchedEffect(message) {
        println("Received message: $message")
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color(0xFF1E1E1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "WebSocket Test",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Connection Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (isConnected) Color.Green else Color.Red,
                            shape = CircleShape
                        )
                )
                Text(
                    text = if (isConnected) " Connected" else " Disconnected",
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Input Section
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                label = { Text("Enter message") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color(0xFF2196F3),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF2196F3),
                    unfocusedLabelColor = Color.Gray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Button(
                onClick = {
                    if (inputMessage.isNotBlank()) {
                        onMessageSend(inputMessage)
                        inputMessage = ""
                    }
                },
                modifier = Modifier.align(Alignment.End),
                enabled = isConnected && inputMessage.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF2196F3),
                    disabledBackgroundColor = Color.Gray
                )
            ) {
                Text("Send")
            }

            // Echo Response
            if (message.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    backgroundColor = Color(0xFF0A1929)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Echo Response:",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = message,
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}