package com.project.fighthub

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform