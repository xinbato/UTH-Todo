package com.example.uthtodo.data.repository

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Contract for application settings persistence.
 */
interface SettingsRepository {
    val themeMode: StateFlow<Int> // 0: Sáng, 1: Tối, 2: Tự động
    fun setThemeMode(mode: Int)

    val isReminderEnabled: StateFlow<Boolean>
    fun setReminderEnabled(enabled: Boolean)

    val isSoundEnabled: StateFlow<Boolean>
    fun setSoundEnabled(enabled: Boolean)

    val isSummaryEnabled: StateFlow<Boolean>
    fun setSummaryEnabled(enabled: Boolean)

    val primaryColorIndex: Flow<Int> // 0: Blue, 1: Green, 2: Orange, 3: Purple
    suspend fun setPrimaryColorIndex(index: Int)

    val fontScale: Flow<Float> // 0.85f: Nhỏ, 1.0f: Chuẩn, 1.15f: Lớn
    suspend fun setFontScale(scale: Float)

    val languageCode: StateFlow<String> // "vi": Tiếng Việt, "en": English
    fun setLanguageCode(code: String)
}

/**
 * Concrete implementation using Android SharedPreferences (0 extra dependencies).
 */
class SharedPreferencesSettingsRepository(context: Context) : SettingsRepository {
    private val prefs = context.getSharedPreferences("uth_todo_settings", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(prefs.getInt(KEY_THEME_MODE, 2))
    override val themeMode: StateFlow<Int> = _themeMode.asStateFlow()

    private val _isReminderEnabled = MutableStateFlow(prefs.getBoolean(KEY_REMINDER_ENABLED, true))
    override val isReminderEnabled: StateFlow<Boolean> = _isReminderEnabled.asStateFlow()

    private val _isSoundEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND_ENABLED, true))
    override val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val _isSummaryEnabled = MutableStateFlow(prefs.getBoolean(KEY_SUMMARY_ENABLED, true))
    override val isSummaryEnabled: StateFlow<Boolean> = _isSummaryEnabled.asStateFlow()

    private val _primaryColorIndex = MutableStateFlow(prefs.getInt(KEY_PRIMARY_COLOR_INDEX, 0))
    override val primaryColorIndex: StateFlow<Int> = _primaryColorIndex.asStateFlow()

    private val _fontScale = MutableStateFlow(prefs.getFloat(KEY_FONT_SCALE, 1.0f))
    override val fontScale: StateFlow<Float> = _fontScale.asStateFlow()

    private val _languageCode = MutableStateFlow(prefs.getString(KEY_LANGUAGE_CODE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE)
    override val languageCode: StateFlow<String> = _languageCode.asStateFlow()

    override fun setThemeMode(mode: Int) {
        prefs.edit { putInt(KEY_THEME_MODE, mode) }
        _themeMode.value = mode
    }

    override fun setReminderEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_REMINDER_ENABLED, enabled) }
        _isReminderEnabled.value = enabled
    }

    override fun setSoundEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_SOUND_ENABLED, enabled) }
        _isSoundEnabled.value = enabled
    }

    override fun setSummaryEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_SUMMARY_ENABLED, enabled) }
        _isSummaryEnabled.value = enabled
    }

    override suspend fun setPrimaryColorIndex(index: Int) {
        prefs.edit { putInt(KEY_PRIMARY_COLOR_INDEX, index) }
        _primaryColorIndex.value = index
    }

    override suspend fun setFontScale(scale: Float) {
        prefs.edit { putFloat(KEY_FONT_SCALE, scale) }
        _fontScale.value = scale
    }

    override fun setLanguageCode(code: String) {
        prefs.edit { putString(KEY_LANGUAGE_CODE, code) }
        _languageCode.value = code
    }

    companion object {
        const val DEFAULT_LANGUAGE = "vi"
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_REMINDER_ENABLED = "key_reminder_enabled"
        private const val KEY_SOUND_ENABLED = "key_sound_enabled"
        private const val KEY_SUMMARY_ENABLED = "key_summary_enabled"
        private const val KEY_PRIMARY_COLOR_INDEX = "key_primary_color_index"
        private const val KEY_FONT_SCALE = "key_font_scale"
        const val KEY_LANGUAGE_CODE = "key_language_code"

        fun getSavedLanguage(context: Context): String {
            val prefs = context.getSharedPreferences("uth_todo_settings", Context.MODE_PRIVATE)
            return prefs.getString(KEY_LANGUAGE_CODE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        }
    }
}
