package org.sj.cricradio

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform