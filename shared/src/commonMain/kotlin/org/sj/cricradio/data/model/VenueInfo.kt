package org.sj.cricradio.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VenueInfoResponse(
    val statusCode: Int,
    val responseData: ResponseData,
    val requestParams: Map<String, String> = emptyMap(),
    val time: String
)

@Serializable
data class ResponseData(
    val message: String,
    val result: VenueResult
)

@Serializable
data class VenueResult(
    @SerialName("_id")
    val id: String,
    val firstUmpire: String,
    val format: String,
    val key: String,
    val matchReferee: String,
    @SerialName("related_name")
    val relatedName: String,
    val season: Season,
    val secoundUmpire: String,
    @SerialName("start_date")
    val startDate: StartDate,
    val status: String,
    val teams: Teams,
    val thirdUmpire: String,
    val toss: Toss,
    val venue: String,
    val venueDetails: VenueDetails,
    val weather: Weather,
    val venueStats: VenueStats
)

@Serializable
data class Season(
    val key: String,
    val name: String
)

@Serializable
data class StartDate(
    val timestamp: Long,
    val iso: String,
    val str: String,
    @SerialName("sky_check_ts")
    val skyCheckTs: Long
)

@Serializable
data class Teams(
    val a: Team,
    val b: Team
)

@Serializable
data class Team(
    val name: String,
    val shortName: String,
    val logo: String
)

@Serializable
data class Toss(
    val won: String,
    val decision: String,
    val str: String
)

@Serializable
data class VenueDetails(
    @SerialName("_id")
    val id: String,
    val knownAs: String,
    val capacity: Int,
    val createdAt: String,
    val cricinfoId: Int,
    val description: String,
    val ends1: String,
    val ends2: String,
    val floodLights: Int,
    val homeTo: String,
    val isDeleted: String,
    val opened: String? = null,
    val photo: String,
    val status: String,
    val timezone: String,
    val updatedAt: String,
    val venueLocation: String,
    val venueScraptitle: String,
    @SerialName("venue_info")
    val venueInfo: VenueInfo
)

@Serializable
data class VenueInfo(
    val name: String,
    val smallName: String,
    val longName: String,
    val location: String,
    val town: String
)

@Serializable
data class Weather(
    val location: String,
    val condition: WeatherCondition,
    @SerialName("chance_of_rain")
    val chanceOfRain: Int,
    @SerialName("temp_c")
    val tempC: Double,
    @SerialName("last_updated")
    val lastUpdated: String
)

@Serializable
data class WeatherCondition(
    val code: Int,
    val icon: String,
    val text: String
)

@Serializable
data class VenueStats(
    val matchesPlayed: Int,
    val lowestDefended: Int,
    val highestChased: Int,
    val batFirstWins: Int,
    val ballFirstWins: Int,
    val battingFirst: BattingStats,
    val battingSecond: BattingStats
)

@Serializable
data class BattingStats(
    val averageScore: Int,
    val highestScore: Int,
    val lowestScore: Int,
    val paceWickets: Int,
    val spinWickets: Int
)
