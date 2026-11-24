package com.example.model

data class VoidSettings(
    val masterEnabled: Boolean = true,
    val instagramEnabled: Boolean = true,
    val youtubeEnabled: Boolean = true,
    val facebookEnabled: Boolean = true,
    val redirectMode: RedirectMode = RedirectMode.NAVIGATE_BACK,
    val showInterceptionNotice: Boolean = true,
    val instagramBlockedCount: Int = 0,
    val youtubeBlockedCount: Int = 0,
    val facebookBlockedCount: Int = 0,
) {
    fun isAppEnabled(app: SupportedApp): Boolean = when (app) {
        SupportedApp.INSTAGRAM -> instagramEnabled
        SupportedApp.YOUTUBE -> youtubeEnabled
        SupportedApp.FACEBOOK -> facebookEnabled
    }

    fun blockedCount(app: SupportedApp): Int = when (app) {
        SupportedApp.INSTAGRAM -> instagramBlockedCount
        SupportedApp.YOUTUBE -> youtubeBlockedCount
        SupportedApp.FACEBOOK -> facebookBlockedCount
    }

    val totalBlockedCount: Int
        get() = instagramBlockedCount + youtubeBlockedCount + facebookBlockedCount

    val armedTargetCount: Int
        get() = listOf(instagramEnabled, youtubeEnabled, facebookEnabled).count { it }
}
