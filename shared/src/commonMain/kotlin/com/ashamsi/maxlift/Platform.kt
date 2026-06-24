package com.ashamsi.maxlift

/**
 * Supported platform types.
 */
enum class PlatformType { Android, IOS }

/**
 * Interface representing the current platform.
 */
interface Platform {
    /** Name of the platform. */
    val name: String
    /** Type of the platform. */
    val type: PlatformType
    /** Whether the app is running in a debug/development environment. */
    val isDebug: Boolean
}

/**
 * Retrieves the current [Platform].
 */
expect fun getPlatform(): Platform
