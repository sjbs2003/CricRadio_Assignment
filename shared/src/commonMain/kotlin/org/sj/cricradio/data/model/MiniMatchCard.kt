package org.sj.cricradio.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MiniMatchCardResponse(
    val statusCode: Int,
    val responseData: MiniMatchResponseData,
    val requestParams: Map<String, String> = emptyMap(),
    val time: String
)

@Serializable
data class MiniMatchResponseData(
    val message: String,
    val result: MatchResult
)

@Serializable
data class MatchResult(
    val powerplay: String,
    val powerplayOver: Int,
    val key: String,
    val status: String,
    val format: String,
    val announcement1: String,
    val teams: MatchTeams,
    val now: CurrentStats,
    val currentBattingOrder: Int,
    val settingObj: SettingObject,
    val lastCommentary: Commentary,
    val announcement2: String? = null
)

@Serializable
data class MatchTeams(
    val a: TeamScore,
    val b: TeamScore
)

@Serializable
data class TeamScore(
    val name: String,
    val shortName: String,
    val logo: String,
    @SerialName("a_1_score")
    val firstInningsScore: InningsScore? = null,
    @SerialName("a_2_score")
    val secondInningsScore: InningsScore? = null,
    @SerialName("b_1_score")
    val bFirstInningsScore: InningsScore? = null,
    @SerialName("b_2_score")
    val bSecondInningsScore: InningsScore? = null
)

@Serializable
data class InningsScore(
    val runs: Int,
    val overs: String,
    val wickets: Int,
    val declare: Boolean
)

@Serializable
data class CurrentStats(
    @SerialName("run_rate")
    val runRate: String,
    @SerialName("req_run_rate")
    val reqRunRate: String,
    val sessionLeft: String? = null
)

@Serializable
data class SettingObject(
    val currentTeam: String,
    val currentInning: Int
)

@Serializable
data class Commentary(
    val primaryText: String,
    val secondaryText: String? = null,
    val tertiaryText: String? = null,
    val isDone: Boolean
)
