package com.ashamsi.maxlift

enum class PlatformType { Android, IOS }

interface Platform {
    val name: String
    val type: PlatformType
}

expect fun getPlatform(): Platform