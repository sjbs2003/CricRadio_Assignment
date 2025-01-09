package org.sj.cricradio.Network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.sj.cricradio.data.remote.ApiService
import org.sj.cricradio.data.repository.MatchRepoImpl
import org.sj.cricradio.data.repository.MatchRepository

val networkModule = module {
    single { createJson() }
    single { createHttpClient(get()) }
    single { ApiService(get()) }
    single<MatchRepository> { MatchRepoImpl(get(), get()) }
}

fun createJson() = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}

fun createHttpClient(json: Json) = HttpClient(Android) {
    install(ContentNegotiation) {
        json(json)
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                println("Http Client: $message")
            }
        }
        level = LogLevel.ALL
    }

    install(ResponseObserver) {
        onResponse { response ->
            println("Http Response : ${response.status.value}")
        }
    }

    install(DefaultRequest) {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }

    install(WebSockets) {
        pingInterval = 20_000
    }
}