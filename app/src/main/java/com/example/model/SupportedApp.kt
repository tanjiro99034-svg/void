package com.example.model

import androidx.annotation.StringRes
import com.example.R

enum class SupportedApp(
    val id: String,
    @param:StringRes val displayNameRes: Int,
    @param:StringRes val protectionLabelRes: Int,
    @param:StringRes val allowedFeaturesRes: Int,
    val packageNames: List<String>,
) {
    INSTAGRAM(
        id = "instagram",
        displayNameRes = R.string.app_instagram,
        protectionLabelRes = R.string.target_instagram,
        allowedFeaturesRes = R.string.allowed_instagram,
        packageNames = listOf("com.instagram.android"),
    ),
    YOUTUBE(
        id = "youtube",
        displayNameRes = R.string.app_youtube,
        protectionLabelRes = R.string.target_youtube,
        allowedFeaturesRes = R.string.allowed_youtube,
        packageNames = listOf("com.google.android.youtube"),
    ),
    FACEBOOK(
        id = "facebook",
        displayNameRes = R.string.app_facebook,
        protectionLabelRes = R.string.target_facebook,
        allowedFeaturesRes = R.string.allowed_facebook,
        packageNames = listOf("com.facebook.katana", "com.facebook.lite"),
    );

    companion object {
        fun fromPackageName(packageName: String?): SupportedApp? {
            if (packageName.isNullOrBlank()) return null
            return entries.firstOrNull { app -> packageName in app.packageNames }
        }
    }
}
