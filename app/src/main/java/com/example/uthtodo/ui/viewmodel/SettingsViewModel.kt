package com.example.uthtodo.ui.viewmodel

import android.app.Application
import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uthtodo.data.repository.SettingsRepository
import com.example.uthtodo.data.repository.SharedPreferencesSettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel managing app settings, theme preferences, and notification preferences.
 */
class SettingsViewModel @JvmOverloads constructor(
    application: Application,
    private val settingsRepository: SettingsRepository = SharedPreferencesSettingsRepository(application)
) : AndroidViewModel(application) {

    val themeMode: StateFlow<Int> = settingsRepository.themeMode
    val isReminderEnabled: StateFlow<Boolean> = settingsRepository.isReminderEnabled
    val isSoundEnabled: StateFlow<Boolean> = settingsRepository.isSoundEnabled
    val isSummaryEnabled: StateFlow<Boolean> = settingsRepository.isSummaryEnabled

    val primaryColorIndex: StateFlow<Int> = (settingsRepository.primaryColorIndex as? StateFlow<Int>)
        ?: settingsRepository.primaryColorIndex.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0
        )

    val fontScale: StateFlow<Float> = (settingsRepository.fontScale as? StateFlow<Float>)
        ?: settingsRepository.fontScale.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 1.0f
        )

    val languageCode: StateFlow<String> = settingsRepository.languageCode

    fun setThemeMode(mode: Int) {
        settingsRepository.setThemeMode(mode)
    }

    fun setReminderEnabled(enabled: Boolean) {
        settingsRepository.setReminderEnabled(enabled)
    }

    fun setSoundEnabled(enabled: Boolean) {
        settingsRepository.setSoundEnabled(enabled)
    }

    fun setSummaryEnabled(enabled: Boolean) {
        settingsRepository.setSummaryEnabled(enabled)
    }

    fun setPrimaryColorIndex(index: Int) {
        viewModelScope.launch {
            settingsRepository.setPrimaryColorIndex(index)
        }
    }

    fun setFontScale(scale: Float) {
        viewModelScope.launch {
            settingsRepository.setFontScale(scale)
        }
    }

    fun setLanguageCode(code: String) {
        settingsRepository.setLanguageCode(code)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getApplication<Application>().getSystemService(LocaleManager::class.java)?.applicationLocales =
                LocaleList.forLanguageTags(code)
        }
    }
}
