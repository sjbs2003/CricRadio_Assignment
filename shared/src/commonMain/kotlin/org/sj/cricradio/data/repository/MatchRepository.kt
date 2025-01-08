package org.sj.cricradio.data.repository

import kotlinx.coroutines.flow.Flow
import org.sj.cricradio.data.model.MiniMatchCardResponse
import org.sj.cricradio.data.model.VenueInfoResponse

interface MatchRepository {

    suspend fun getMiniMatchCard(matchKey: String) : Result<MiniMatchCardResponse>
    suspend fun getVenueInfo(matchKey: String): Result<VenueInfoResponse>
    suspend fun connectToWebSocket(): Result<Unit>
    suspend fun sendMessage(message: String): Result<Unit>
    suspend fun observeWebSocketMessage(): Flow<String>
}