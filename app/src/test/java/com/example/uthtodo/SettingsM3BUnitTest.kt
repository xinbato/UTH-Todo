package com.example.uthtodo

import com.example.uthtodo.data.repository.SettingsRepository
import com.example.uthtodo.ui.theme.PrimaryColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsM3BUnitTest {

    private class FakeSettingsRepository : SettingsRepository {
        private val _themeMode = MutableStateFlow(2)
        override val themeMode: StateFlow<Int> = _themeMode.asStateFlow()

        private val _isReminderEnabled = MutableStateFlow(true)
        override val isReminderEnabled: StateFlow<Boolean> = _isReminderEnabled.asStateFlow()

        private val _isSoundEnabled = MutableStateFlow(true)
        override val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

        private val _isSummaryEnabled = MutableStateFlow(false)
        override val isSummaryEnabled: StateFlow<Boolean> = _isSummaryEnabled.asStateFlow()

        private val _primaryColorIndex = MutableStateFlow(0)
        override val primaryColorIndex: StateFlow<Int> = _primaryColorIndex.asStateFlow()

        private val _fontScale = MutableStateFlow(1.0f)
        override val fontScale: StateFlow<Float> = _fontScale.asStateFlow()

        override fun setThemeMode(mode: Int) {
            _themeMode.value = mode
        }

        override fun setReminderEnabled(enabled: Boolean) {
            _isReminderEnabled.value = enabled
        }

        override fun setSoundEnabled(enabled: Boolean) {
            _isSoundEnabled.value = enabled
        }

        override fun setSummaryEnabled(enabled: Boolean) {
            _isSummaryEnabled.value = enabled
        }

        override suspend fun setPrimaryColorIndex(index: Int) {
            _primaryColorIndex.value = index
        }

        override suspend fun setFontScale(scale: Float) {
            _fontScale.value = scale
        }
    }

    @Test
    fun testPrimaryColorsListHasFourOptions() {
        assertEquals(4, PrimaryColors.size)
        assertEquals(androidx.compose.ui.graphics.Color(0xFF0066FF), PrimaryColors[0])
        assertEquals(androidx.compose.ui.graphics.Color(0xFF0F7A4F), PrimaryColors[1])
        assertEquals(androidx.compose.ui.graphics.Color(0xFFEA580C), PrimaryColors[2])
        assertEquals(androidx.compose.ui.graphics.Color(0xFF7C3AED), PrimaryColors[3])
    }

    @Test
    fun testSettingsRepositoryColorAndFontScaleFlows() = runBlocking {
        val repo = FakeSettingsRepository()

        // Initial default checks
        assertEquals(0, repo.primaryColorIndex.first())
        assertEquals(1.0f, repo.fontScale.first(), 0.001f)

        // Mutate color index
        repo.setPrimaryColorIndex(2)
        assertEquals(2, repo.primaryColorIndex.first())

        // Mutate font scale
        repo.setFontScale(1.15f)
        assertEquals(1.15f, repo.fontScale.first(), 0.001f)

        repo.setFontScale(0.85f)
        assertEquals(0.85f, repo.fontScale.first(), 0.001f)
    }
}
