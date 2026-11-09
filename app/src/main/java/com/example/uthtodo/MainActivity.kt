package com.example.uthtodo

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uthtodo.data.repository.SharedPreferencesSettingsRepository
import com.example.uthtodo.navigation.AppNavigation
import com.example.uthtodo.notification.NotificationHelper
import com.example.uthtodo.ui.theme.UTHTodoTheme
import com.example.uthtodo.ui.viewmodel.SettingsViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun attachBaseContext(newBase: Context) {
        val lang = SharedPreferencesSettingsRepository.getSavedLanguage(newBase)
        val config = Configuration(newBase.resources.configuration).apply {
            val locale = Locale.forLanguageTag(lang)
            setLocale(locale)
            setLayoutDirection(locale)
        }
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Notification channel and request permission if needed
        NotificationHelper.createNotificationChannel(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsStateWithLifecycle()
            val primaryColorIndex by settingsViewModel.primaryColorIndex.collectAsStateWithLifecycle()
            val fontScale by settingsViewModel.fontScale.collectAsStateWithLifecycle()
            val languageCode by settingsViewModel.languageCode.collectAsStateWithLifecycle()

            val context = LocalContext.current
            val currentConfiguration = LocalConfiguration.current
            val localizedConfiguration = remember(languageCode, currentConfiguration) {
                Configuration(currentConfiguration).apply {
                    val locale = Locale.forLanguageTag(languageCode)
                    setLocale(locale)
                    setLayoutDirection(locale)
                }
            }
            val localizedContext = remember(languageCode, context) {
                context.createConfigurationContext(localizedConfiguration)
            }

            val isDark = when (themeMode) {
                0 -> false
                1 -> true
                else -> isSystemInDarkTheme()
            }
            UTHTodoTheme(darkTheme = isDark, primaryColorIndex = primaryColorIndex) {
                val currentDensity = LocalDensity.current
                CompositionLocalProvider(
                    LocalConfiguration provides localizedConfiguration,
                    LocalContext provides localizedContext,
                    LocalDensity provides Density(
                        density = currentDensity.density,
                        fontScale = fontScale
                    )
                ) {
                    AppNavigation(settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}
