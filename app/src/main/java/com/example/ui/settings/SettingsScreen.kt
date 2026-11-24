package com.example.ui.settings

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.RedirectMode
import com.example.model.SupportedApp
import com.example.ui.VoidUiState

@Composable
fun SettingsScreen(
    uiState: VoidUiState,
    onNavigateBack: () -> Unit,
    onOpenAccessibilitySettings: () -> Unit,
    onRedirectModeSelected: (RedirectMode) -> Unit,
    onShowInterceptionNoticeChanged: (Boolean) -> Unit,
    onResetCounters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter,
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp)
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item(key = "settings_header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = CircleShape,
                                )
                                .testTag("settings_back_button"),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = stringResource(R.string.cd_back_button),
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = stringResource(R.string.settings_title),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }

                // Accessibility Engine Section
                item(key = "accessibility_engine") {
                    SettingsSectionCard(
                        title = stringResource(R.string.settings_service_section),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (uiState.isAccessibilityEnabled) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f)
                                        },
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccessibilityNew,
                                    contentDescription = null,
                                    tint = if (uiState.isAccessibilityEnabled) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.tertiary
                                    },
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.accessibility_service_label),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                Text(
                                    text = stringResource(
                                        if (uiState.isAccessibilityEnabled) {
                                            R.string.settings_service_connected
                                        } else {
                                            R.string.settings_service_disconnected
                                        },
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (uiState.isAccessibilityEnabled) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.tertiary
                                    },
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onOpenAccessibilitySettings,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("open_accessibility_settings_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.isAccessibilityEnabled) {
                                    MaterialTheme.colorScheme.surfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                                contentColor = if (uiState.isAccessibilityEnabled) {
                                    MaterialTheme.colorScheme.onBackground
                                } else {
                                    MaterialTheme.colorScheme.onPrimary
                                },
                            ),
                        ) {
                            Text(
                                text = stringResource(R.string.settings_open_android_accessibility),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }

                // Interception Behavior Section
                item(key = "interception_behavior") {
                    SettingsSectionCard(
                        title = stringResource(R.string.settings_behavior_section),
                    ) {
                        Text(
                            text = stringResource(R.string.redirect_mode_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        RedirectOptionRow(
                            title = stringResource(R.string.redirect_mode_back_title),
                            description = stringResource(R.string.redirect_mode_back_desc),
                            selected = uiState.settings.redirectMode == RedirectMode.NAVIGATE_BACK,
                            onClick = { onRedirectModeSelected(RedirectMode.NAVIGATE_BACK) },
                            testTag = "redirect_mode_back",
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        RedirectOptionRow(
                            title = stringResource(R.string.redirect_mode_home_title),
                            description = stringResource(R.string.redirect_mode_home_desc),
                            selected = uiState.settings.redirectMode == RedirectMode.GO_HOME,
                            onClick = { onRedirectModeSelected(RedirectMode.GO_HOME) },
                            testTag = "redirect_mode_home",
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            color = MaterialTheme.colorScheme.outline,
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    role = Role.Switch,
                                    onClick = {
                                        onShowInterceptionNoticeChanged(
                                            !uiState.settings.showInterceptionNotice,
                                        )
                                    },
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.NotificationsActive,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.show_notice_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                Text(
                                    text = stringResource(R.string.show_notice_desc),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = uiState.settings.showInterceptionNotice,
                                onCheckedChange = onShowInterceptionNoticeChanged,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("switch_interception_notice"),
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
                }

                // Telemetry Section
                item(key = "telemetry") {
                    SettingsSectionCard(
                        title = stringResource(R.string.settings_telemetry_section),
                    ) {
                        SupportedApp.entries.forEachIndexed { index, app ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "${stringResource(app.displayNameRes)} — ${stringResource(app.protectionLabelRes)}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                Text(
                                    text = stringResource(
                                        R.string.blocked_count_format,
                                        uiState.settings.blockedCount(app),
                                    ),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                            if (index < SupportedApp.entries.lastIndex) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.reset_counters_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                Text(
                                    text = stringResource(R.string.reset_counters_desc),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            OutlinedButton(
                                onClick = onResetCounters,
                                enabled = uiState.settings.totalBlockedCount > 0,
                                modifier = Modifier
                                    .height(44.dp)
                                    .testTag("reset_counters_button"),
                                shape = RoundedCornerShape(10.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.RestartAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.reset_counters_button),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }
                }

                // Scope & Transparency Section
                item(key = "scope_transparency") {
                    SettingsSectionCard(
                        title = stringResource(R.string.settings_scope_section),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(R.string.settings_scope_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.settings_scope_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                item(key = "settings_bottom_spacer") {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = shape,
            )
            .padding(18.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun RedirectOptionRow(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String,
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.background.copy(alpha = 0.6f)
                },
            )
            .border(
                width = 1.dp,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                } else {
                    MaterialTheme.colorScheme.outline
                },
                shape = shape,
            )
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
