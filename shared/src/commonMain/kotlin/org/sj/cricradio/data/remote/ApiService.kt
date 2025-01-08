package org.sj.cricradio.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import org.sj.cricradio.data.model.MiniMatchCardResponse
import org.sj.cricradio.data.model.VenueInfoResponse

class ApiService(private val client: HttpClient) {

    companion object {
        private const val BASE_URL = "http://3.6.243.12:5001"
        private const val AUTH_HEADER = "Basic Y3JpY2tldFJhZGlvOmNyaWNrZXRAJCUjUmFkaW8xMjM="
    }

    suspend fun getMiniMatchCard(matchKey: String): MiniMatchCardResponse {
        return client.get("$BASE_URL/api/v2/match/mini-match-card") {
            parameter("key", matchKey)
            header(HttpHeaders.Authorization, AUTH_HEADER)
        }.body()
    }

    suspend fun getVenueInfo(matchKey: String): VenueInfoResponse {
        return client.get("$BASE_URL/api/v2/match/venue-info") {
            parameter("key", matchKey)
            header(HttpHeaders.Authorization, AUTH_HEADER)
        }.body()
    }
}