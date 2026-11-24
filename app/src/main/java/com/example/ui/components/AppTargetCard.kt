package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.SupportedApp

@Composable
fun AppTargetCard(
    app: SupportedApp,
    isAppEnabled: Boolean,
    isSystemActive: Boolean,
    isInstalled: Boolean,
    blockedCount: Int,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val effectivelyArmed = isAppEnabled && isSystemActive
    val borderColor by animateColorAsState(
        targetValue = when {
            effectivelyArmed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
            isAppEnabled -> MaterialTheme.colorScheme.outline.copy(alpha = 0.9f)
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
        },
        animationSpec = tween(durationMillis = 220),
        label = "cardBorderColor",
    )

    val cardShape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                    ),
                ),
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = cardShape,
            )
            .clickable(
                role = Role.Switch,
                onClick = { onToggle(!isAppEnabled) },
            )
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .testTag("app_card_${app.id}"),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Minimalistic emblem tile
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f))
                        .border(
                            width = 1.dp,
                            color = if (effectivelyArmed) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                            } else {
                                MaterialTheme.colorScheme.outline
                            },
                            shape = RoundedCornerShape(14.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    AppTargetEmblem(
                        app = app,
                        isArmed = isAppEnabled,
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(app.displayNameRes),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = stringResource(
                                if (isInstalled) R.string.badge_installed else R.string.badge_not_installed,
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isInstalled) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            },
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = stringResource(app.protectionLabelRes),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isAppEnabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }

                Switch(
                    checked = isAppEnabled,
                    onCheckedChange = onToggle,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("switch_${app.id}"),
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(app.allowedFeaturesRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (effectivelyArmed) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.background.copy(alpha = 0.65f)
                            },
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (effectivelyArmed) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            ),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(
                            if (isAppEnabled) R.string.badge_protected else R.string.badge_bypassed,
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (effectivelyArmed) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }

                Text(
                    text = stringResource(R.string.blocked_count_format, blockedCount),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
