package com.example.service

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.example.R
import com.example.data.VoidSettingsRepository
import com.example.model.RedirectMode
import com.example.model.SupportedApp
import com.example.model.VoidSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VoidAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var repository: VoidSettingsRepository

    @Volatile
    private var currentSettings: VoidSettings = VoidSettings()

    private val lastInterceptElapsedMs = mutableMapOf<SupportedApp, Long>()
    private var lastToastElapsedMs: Long = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        repository = VoidSettingsRepository.getInstance(applicationContext)
        serviceScope.launch {
            repository.settingsFlow.collectLatest { settings ->
                currentSettings = settings
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val settings = currentSettings
        if (!settings.masterEnabled) return

        val packageName = event.packageName?.toString()
        val targetApp = SupportedApp.fromPackageName(packageName) ?: return
        if (!settings.isAppEnabled(targetApp)) return

        val now = SystemClock.elapsedRealtime()
        val lastAppIntercept = lastInterceptElapsedMs[targetApp] ?: 0L
        if (now - lastAppIntercept < INTERCEPT_COOLDOWN_MS) return

        val root = rootInActiveWindow ?: return
        val detection = try {
            DistractionDetector.detect(targetApp, root)
        } finally {
            @Suppress("DEPRECATION")
            root.recycle()
        }

        if (detection != null) {
            lastInterceptElapsedMs[targetApp] = now
            val globalAction = when (settings.redirectMode) {
                RedirectMode.NAVIGATE_BACK -> GLOBAL_ACTION_BACK
                RedirectMode.GO_HOME -> GLOBAL_ACTION_HOME
            }
            performGlobalAction(globalAction)

            serviceScope.launch {
                repository.incrementBlockedCount(targetApp)
            }

            if (settings.showInterceptionNotice && now - lastToastElapsedMs > TOAST_COOLDOWN_MS) {
                lastToastElapsedMs = now
                val message = getString(R.string.intercept_toast_format, detection.featureLabel)
                mainHandler.post {
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onInterrupt() {
        // No ongoing speech or audio feedback to interrupt.
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        private const val INTERCEPT_COOLDOWN_MS = 1200L
        private const val TOAST_COOLDOWN_MS = 2500L
    }
}
