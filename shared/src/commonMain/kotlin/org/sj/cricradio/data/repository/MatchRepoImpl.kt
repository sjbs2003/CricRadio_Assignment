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
            println("Attempting to connect to WebSocket...")
            webSocketSession = client.webSocketSession {
                url("wss://ws.postmanecho.com/raw")

                // Essential WebSocket headers
                header("Connection", "Upgrade")
                header("Upgrade", "websocket")
                header("Sec-WebSocket-Version", "13")
                header("Sec-WebSocket-Key", generateNonce())
            }

            println("WebSocket connection established")
            startListening()
            Result.success(Unit)
        } catch (e: Exception) {
            println("WebSocket connection failed: ${e.message}")
            e.printStackTrace() // Add this to get full stack trace
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
            println("Starting to listen for WebSocket messages")
            session.incoming
                .consumeAsFlow()
                .collect { frame ->
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            println("Received WebSocket message: $text")
                            messageChannel.send(text)
                        }
                        else -> println("Received non-text frame: $frame")
                    }
                }
        } catch (e: Exception) {
            println("Error in WebSocket listener: ${e.message}")
            messageChannel.close(e)
        }
    }

    suspend fun cleanup() {
        webSocketSession?.close()
        webSocketSession = null
        messageChannel.close()
    }
}