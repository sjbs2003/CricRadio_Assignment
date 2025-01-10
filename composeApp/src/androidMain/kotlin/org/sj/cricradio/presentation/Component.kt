package org.sj.cricradio.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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