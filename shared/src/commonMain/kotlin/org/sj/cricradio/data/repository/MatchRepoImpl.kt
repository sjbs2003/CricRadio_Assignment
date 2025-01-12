package org.sj.cricradio.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.util.generateNonce
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import org.sj.cricradio.data.model.MiniMatchCardResponse
import org.sj.cricradio.data.model.VenueInfoResponse
import org.sj.cricradio.data.remote.ApiService

class MatchRepoImpl(
    private val apiService: ApiService,
    private val client: HttpClient
): MatchRepository {

    private var webSocketSession: WebSocketSession? = null
    private val messageChannel = Channel<String>()

    override suspend fun getMiniMatchCard(matchKey: String): Result<MiniMatchCardResponse> {
        return try {
            Result.success(apiService.getMiniMatchCard(matchKey))
        } catch (e:Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getVenueInfo(matchKey: String): Result<VenueInfoResponse> {
        return try {
            Result.success(apiService.getVenueInfo(matchKey))
        } catch (e:Exception) {
            Result.failure(e)
        }
    }

    override suspend fun connectToWebSocket(): Result<Unit> {
        return try {
            webSocketSession = client.webSocketSession {
                url("wss://ws.postman-echo.com/raw")
            }
            startListening()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendMessage(message: String): Result<Unit> {
        return try {
            webSocketSession?.send(Frame.Text(message))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun observeWebSocketMessage(): Flow<String> {
        return messageChannel.receiveAsFlow()
    }

    private suspend fun startListening() {
        try {
            val session = webSocketSession ?: return
            session.incoming
                .consumeAsFlow()
                .collect { frame ->
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            messageChannel.send(text)
                        }
                        else -> println("Received non-text frame: $frame")
                    }
                }
        } catch (e: Exception) {
            messageChannel.close(e)
        }
    }

    suspend fun cleanup() {
        webSocketSession?.close()
        webSocketSession = null
        messageChannel.close()
    }
}