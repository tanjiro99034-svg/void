package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AccessibilityHelper
import com.example.data.VoidSettingsRepository
import com.example.model.RedirectMode
import com.example.model.SupportedApp
import com.example.model.VoidSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class VoidUiState(
    val settings: VoidSettings = VoidSettings(),
    val isAccessibilityEnabled: Boolean = false,
    val installedApps: Map<SupportedApp, Boolean> = SupportedApp.entries.associateWith { false },
    val showDisclosureDialog: Boolean = false,
) {
    val isProtectionActive: Boolean
        get() = settings.masterEnabled &&
            isAccessibilityEnabled &&
            settings.armedTargetCount > 0
}

class VoidViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VoidSettingsRepository.getInstance(application)

    private val isAccessibilityEnabledFlow = MutableStateFlow(
        AccessibilityHelper.isVoidServiceEnabled(application),
    )
    private val installedAppsFlow = MutableStateFlow(
        AccessibilityHelper.getInstalledAppMap(application),
    )
    private val showDisclosureDialogFlow = MutableStateFlow(false)

    val uiState: StateFlow<VoidUiState> = combine(
        repository.settingsFlow,
        isAccessibilityEnabledFlow,
        installedAppsFlow,
        showDisclosureDialogFlow,
    ) { settings, accessibilityEnabled, installedApps, showDisclosure ->
        VoidUiState(
            settings = settings,
            isAccessibilityEnabled = accessibilityEnabled,
            installedApps = installedApps,
            showDisclosureDialog = showDisclosure,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VoidUiState(
            isAccessibilityEnabled = isAccessibilityEnabledFlow.value,
            installedApps = installedAppsFlow.value,
        ),
    )

    fun refreshSystemState() {
        val context = getApplication<Application>()
        isAccessibilityEnabledFlow.value = AccessibilityHelper.isVoidServiceEnabled(context)
        installedAppsFlow.value = AccessibilityHelper.getInstalledAppMap(context)
    }

    fun onMasterToggleChanged(enabled: Boolean) {
        viewModelScope.launch {
            repository.setMasterEnabled(enabled)
            if (enabled && !isAccessibilityEnabledFlow.value) {
                showDisclosureDialogFlow.value = true
            }
        }
    }

    fun onOrbClicked() {
        if (!isAccessibilityEnabledFlow.value) {
            showDisclosureDialogFlow.value = true
        } else {
            onMasterToggleChanged(!uiState.value.settings.masterEnabled)
        }
    }

    fun onAppToggleChanged(app: SupportedApp, enabled: Boolean) {
        viewModelScope.launch {
            repository.setAppEnabled(app, enabled)
        }
    }

    fun onRedirectModeSelected(mode: RedirectMode) {
        viewModelScope.launch {
            repository.setRedirectMode(mode)
        }
    }

    fun onShowInterceptionNoticeChanged(show: Boolean) {
        viewModelScope.launch {
            repository.setShowInterceptionNotice(show)
        }
    }

    fun onResetCounters() {
        viewModelScope.launch {
            repository.resetBlockedCounts()
        }
    }

    fun openDisclosureDialog() {
        showDisclosureDialogFlow.value = true
    }

    fun dismissDisclosureDialog() {
        showDisclosureDialogFlow.value = false
    }

    fun openAndroidAccessibilitySettings() {
        showDisclosureDialogFlow.value = false
        AccessibilityHelper.openAccessibilitySettings(getApplication())
    }
}
