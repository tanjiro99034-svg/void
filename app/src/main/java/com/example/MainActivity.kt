package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.VoidViewModel
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.VoidTheme

const val ROUTE_DASHBOARD = "dashboard"
const val ROUTE_SETTINGS = "settings"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )
        setContent {
            VoidTheme {
                VoidApp()
            }
        }
    }
}

@Composable
fun VoidApp(
    viewModel: VoidViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshSystemState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    NavHost(
        navController = navController,
        startDestination = ROUTE_DASHBOARD,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
    ) {
        composable(ROUTE_DASHBOARD) {
            DashboardScreen(
                uiState = uiState,
                onNavigateToSettings = {
                    navController.navigate(ROUTE_SETTINGS) {
                        launchSingleTop = true
                    }
                },
                onMasterToggleChanged = viewModel::onMasterToggleChanged,
                onOrbClicked = viewModel::onOrbClicked,
                onAppToggleChanged = viewModel::onAppToggleChanged,
                onOpenDisclosure = viewModel::openDisclosureDialog,
                onDismissDisclosure = viewModel::dismissDisclosureDialog,
                onConfirmDisclosure = viewModel::openAndroidAccessibilitySettings,
            )
        }

        composable(ROUTE_SETTINGS) {
            SettingsScreen(
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() },
                onOpenAccessibilitySettings = viewModel::openAndroidAccessibilitySettings,
                onRedirectModeSelected = viewModel::onRedirectModeSelected,
                onShowInterceptionNoticeChanged = viewModel::onShowInterceptionNoticeChanged,
                onResetCounters = viewModel::onResetCounters,
            )
        }
    }
}
