package org.sj.cricradio.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
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

val networkModule = module {
    single { createJson() }
    single { createHttpClient(get()) }
    single { ApiService(get()) }
}

fun createJson() = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}

fun createHttpClient(json: Json) = HttpClient(CIO) {
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
