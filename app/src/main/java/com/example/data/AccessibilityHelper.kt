package com.example.data

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import com.example.model.SupportedApp
import com.example.service.VoidAccessibilityService

object AccessibilityHelper {

    fun isVoidServiceEnabled(context: Context): Boolean {
        val expectedComponent = ComponentName(context, VoidAccessibilityService::class.java)
        val accessibilityManager =
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager

        val enabledList = accessibilityManager
            ?.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            .orEmpty()

        val foundInManager = enabledList.any { serviceInfo ->
            val resolveService = serviceInfo.resolveInfo?.serviceInfo ?: return@any false
            ComponentName(resolveService.packageName, resolveService.name) == expectedComponent
        }
        if (foundInManager) return true

        val enabledServicesSetting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
        ) ?: return false

        val flattenedName = expectedComponent.flattenToString()
        val shortName = expectedComponent.flattenToShortString()
        return enabledServicesSetting.split(':').any { entry ->
            entry.equals(flattenedName, ignoreCase = true) ||
                entry.equals(shortName, ignoreCase = true)
        }
    }

    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching {
            context.startActivity(intent)
        }
    }

    fun getInstalledAppMap(context: Context): Map<SupportedApp, Boolean> {
        val pm = context.packageManager
        return SupportedApp.entries.associateWith { app ->
            app.packageNames.any { pkg -> isPackageInstalled(pm, pkg) }
        }
    }

    private fun isPackageInstalled(pm: PackageManager, packageName: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(packageName, 0)
            }
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }
}
