package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GppMaybe
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProtectionStatusOrb(
    isProtectionActive: Boolean,
    isMasterEnabled: Boolean,
    isAccessibilityEnabled: Boolean,
    armedTargetCount: Int,
    totalBlockedCount: Int,
    onOrbClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val needsService = isMasterEnabled && !isAccessibilityEnabled

    val coreColor by animateColorAsState(
        targetValue = when {
            isProtectionActive -> MaterialTheme.colorScheme.primary
            needsService -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 280),
        label = "orbCoreColor",
    )

    val infiniteTransition = rememberInfiniteTransition(label = "orbPulse")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )

    val ringGlowAlpha = if (isProtectionActive) pulseProgress else 0.16f

    val statusText = when {
        isProtectionActive -> stringResource(R.string.status_active)
        else -> stringResource(R.string.status_inactive)
    }

    val subtitleText = when {
        isProtectionActive -> stringResource(R.string.status_active_subtitle)
        needsService -> stringResource(R.string.status_inactive_service_needed)
        !isMasterEnabled -> stringResource(R.string.status_inactive_all_off)
        else -> stringResource(R.string.status_inactive_no_targets)
    }

    val statusIcon = when {
        isProtectionActive -> Icons.Outlined.Shield
        needsService -> Icons.Outlined.GppMaybe
        else -> Icons.Outlined.PowerSettingsNew
    }

    val surfaceColor = MaterialTheme.colorScheme.surface
    val borderSubtle = MaterialTheme.colorScheme.outline

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(216.dp)
                .clip(CircleShape)
                .clickable(onClick = onOrbClick)
                .testTag("protection_status_orb"),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val outerRadius = size.minDimension / 2f - 6.dp.toPx()

                // Ambient halo glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            coreColor.copy(alpha = if (isProtectionActive) 0.20f else 0.06f),
                            coreColor.copy(alpha = 0f),
                        ),
                        center = center,
                        radius = outerRadius,
                    ),
                    radius = outerRadius,
                    center = center,
                )

                // Precision segmented outer ring
                val arcInset = 14.dp.toPx()
                val arcSize = Size(
                    width = size.width - arcInset * 2,
                    height = size.height - arcInset * 2,
                )
                drawArc(
                    color = coreColor.copy(alpha = ringGlowAlpha),
                    startAngle = -140f,
                    sweepAngle = 110f,
                    useCenter = false,
                    topLeft = Offset(arcInset, arcInset),
                    size = arcSize,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
                )
                drawArc(
                    color = coreColor.copy(alpha = ringGlowAlpha),
                    startAngle = 40f,
                    sweepAngle = 110f,
                    useCenter = false,
                    topLeft = Offset(arcInset, arcInset),
                    size = arcSize,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
                )

                // Inner circular core border
                val innerRadius = outerRadius - 22.dp.toPx()
                drawCircle(
                    color = surfaceColor.copy(alpha = 0.92f),
                    radius = innerRadius,
                    center = center,
                )
                drawCircle(
                    color = if (isProtectionActive) {
                        coreColor.copy(alpha = 0.65f)
                    } else {
                        borderSubtle
                    },
                    radius = innerRadius,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx()),
                )

                // Subtle radial telemetry ticks
                val tickOuter = outerRadius - 2.dp.toPx()
                val tickInner = outerRadius - 7.dp.toPx()
                for (deg in 0 until 360 step 15) {
                    val rad = Math.toRadians(deg.toDouble())
                    val isCardinal = deg % 90 == 0
                    val tickColor = if (isCardinal) {
                        coreColor.copy(alpha = 0.7f)
                    } else {
                        borderSubtle.copy(alpha = 0.65f)
                    }
                    drawLine(
                        color = tickColor,
                        start = Offset(
                            x = center.x + (tickInner * cos(rad)).toFloat(),
                            y = center.y + (tickInner * sin(rad)).toFloat(),
                        ),
                        end = Offset(
                            x = center.x + (tickOuter * cos(rad)).toFloat(),
                            y = center.y + (tickOuter * sin(rad)).toFloat(),
                        ),
                        strokeWidth = if (isCardinal) 2.dp.toPx() else 1.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = statusText,
                    tint = coreColor,
                    modifier = Modifier.size(30.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isProtectionActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                    modifier = Modifier.testTag("protection_status_label"),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (needsService) {
                        stringResource(R.string.status_service_required)
                    } else {
                        "$armedTargetCount/3 ${stringResource(R.string.targets_armed_label)}"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = coreColor,
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = subtitleText,
            style = MaterialTheme.typography.bodyMedium,
            color = if (needsService) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                .border(
                    width = 1.dp,
                    color = if (isProtectionActive) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                    } else {
                        MaterialTheme.colorScheme.outline
                    },
                    shape = RoundedCornerShape(50),
                )
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(coreColor),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${stringResource(R.string.interceptions_label)}: $totalBlockedCount",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
