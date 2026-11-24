package com.example.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.SupportedApp
import com.example.ui.VoidUiState
import com.example.ui.components.AccessibilityDisclosureDialog
import com.example.ui.components.AppTargetCard
import com.example.ui.components.ProtectionStatusOrb
import com.example.ui.components.VoidLogoMark

@Composable
fun DashboardScreen(
    uiState: VoidUiState,
    onNavigateToSettings: () -> Unit,
    onMasterToggleChanged: (Boolean) -> Unit,
    onOrbClicked: () -> Unit,
    onAppToggleChanged: (SupportedApp, Boolean) -> Unit,
    onOpenDisclosure: () -> Unit,
    onDismissDisclosure: () -> Unit,
    onConfirmDisclosure: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = MaterialTheme.colorScheme.background
    val glowColor = if (uiState.isProtectionActive) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    if (uiState.showDisclosureDialog) {
        AccessibilityDisclosureDialog(
            onConfirm = onConfirmDisclosure,
            onDismiss = onDismissDisclosure,
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = bgColor,
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor),
            contentAlignment = Alignment.TopCenter,
        ) {
            // Subtle atmospheric cyberpunk glow backdrop
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = if (uiState.isProtectionActive) 0.08f else 0.03f),
                            bgColor.copy(alpha = 0f),
                        ),
                        center = Offset(size.width / 2f, size.height * 0.22f),
                        radius = size.maxDimension * 0.52f,
                    ),
                    center = Offset(size.width / 2f, size.height * 0.22f),
                    radius = size.maxDimension * 0.52f,
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp)
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Header: VOID logo, VOID name, Settings icon
                item(key = "header") {
                    DashboardHeader(onNavigateToSettings = onNavigateToSettings)
                }

                // Prominent Circular Status Indicator
                item(key = "status_orb") {
                    ProtectionStatusOrb(
                        isProtectionActive = uiState.isProtectionActive,
                        isMasterEnabled = uiState.settings.masterEnabled,
                        isAccessibilityEnabled = uiState.isAccessibilityEnabled,
                        armedTargetCount = uiState.settings.armedTargetCount,
                        totalBlockedCount = uiState.settings.totalBlockedCount,
                        onOrbClick = onOrbClicked,
                        modifier = Modifier.padding(vertical = 6.dp),
                    )
                }

                // Main Toggle Card to enable or disable all restrictions
                item(key = "master_toggle") {
                    MasterProtectionCard(
                        masterEnabled = uiState.settings.masterEnabled,
                        onToggle = onMasterToggleChanged,
                    )
                }

                // Accessibility Service CTA if service is not yet granted
                item(key = "accessibility_banner") {
                    AnimatedVisibility(
                        visible = !uiState.isAccessibilityEnabled,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        AccessibilityActionBanner(onClick = onOpenDisclosure)
                    }
                }

                // Application Cards Section Header
                item(key = "targets_header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.section_targets),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "${uiState.settings.armedTargetCount}/3",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                // Three Separate Application Cards: Instagram, YouTube, Facebook
                items(
                    items = SupportedApp.entries,
                    key = { it.id },
                ) { app ->
                    AppTargetCard(
                        app = app,
                        isAppEnabled = uiState.settings.isAppEnabled(app),
                        isSystemActive = uiState.settings.masterEnabled && uiState.isAccessibilityEnabled,
                        isInstalled = uiState.installedApps[app] == true,
                        blockedCount = uiState.settings.blockedCount(app),
                        onToggle = { enabled -> onAppToggleChanged(app, enabled) },
                    )
                }

                item(key = "bottom_spacer") {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    onNavigateToSettings: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("void_header_brand"),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(12.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                VoidLogoMark(
                    modifier = Modifier.size(28.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = stringResource(R.string.app_tagline),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        IconButton(
            onClick = onNavigateToSettings,
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = CircleShape,
                )
                .testTag("settings_button"),
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = stringResource(R.string.cd_settings_button),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
private fun MasterProtectionCard(
    masterEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
            .border(
                width = 1.dp,
                color = if (masterEnabled) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                } else {
                    MaterialTheme.colorScheme.outline
                },
                shape = shape,
            )
            .clickable(
                role = Role.Switch,
                onClick = { onToggle(!masterEnabled) },
            )
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .testTag("master_protection_card"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.master_toggle_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(
                    if (masterEnabled) {
                        R.string.master_toggle_desc_on
                    } else {
                        R.string.master_toggle_desc_off
                    },
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = masterEnabled,
            onCheckedChange = onToggle,
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("master_protection_switch"),
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.background,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedBorderColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.background,
                uncheckedBorderColor = MaterialTheme.colorScheme.outline,
            ),
        )
    }
}

@Composable
private fun AccessibilityActionBanner(
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.55f),
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("grant_accessibility_button"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.AccessibilityNew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.enable_service_cta),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(20.dp),
        )
    }
}
