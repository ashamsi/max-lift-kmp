package com.ashamsi.maxlift

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform