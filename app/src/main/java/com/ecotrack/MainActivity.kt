package com.ecotrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ecotrack.core.design.theme.EcoTrackTheme
import com.ecotrack.domain.model.UserSettings
import com.ecotrack.domain.usecase.settings.ObserveSettingsUseCase
import com.ecotrack.navigation.EcoTrackNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var observeSettings: ObserveSettingsUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by observeSettings()
                .collectAsStateWithLifecycle(initialValue = UserSettings())
            val darkTheme = settings.darkTheme ?: isSystemInDarkTheme()
            EcoTrackTheme(
                darkTheme = darkTheme,
                dynamicColor = settings.useDynamicColor,
            ) {
                EcoTrackNavHost()
            }
        }
    }
}
