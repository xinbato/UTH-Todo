package com.example.uthtodo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.R
import com.example.uthtodo.ui.components.UserAvatar
import com.example.uthtodo.ui.theme.BorderGray
import com.example.uthtodo.ui.theme.Dimens
import com.example.uthtodo.ui.theme.PrimaryColors
import com.example.uthtodo.ui.theme.PureWhite
import com.example.uthtodo.ui.theme.TextDark
import com.example.uthtodo.ui.theme.TextMuted
import com.example.uthtodo.ui.theme.UTHTodoTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    themeMode: Int = 2,
    onThemeModeChange: (Int) -> Unit = {},
    primaryColorIndex: Int = 0,
    onPrimaryColorChange: (Int) -> Unit = {},
    fontScale: Float = 1.0f,
    onFontScaleChange: (Float) -> Unit = {},
    avatarInitials: String = "UTH",
    avatarUrl: String? = null,
    isReminderEnabled: Boolean = true,
    onReminderToggle: (Boolean) -> Unit = {},
    isSoundEnabled: Boolean = true,
    onSoundToggle: (Boolean) -> Unit = {},
    isSummaryEnabled: Boolean = false,
    onSummaryToggle: (Boolean) -> Unit = {},
    languageCode: String = "vi",
    onLanguageChange: (String) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {},
    onAddTaskClick: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            HomeBottomNavigation(
                selectedTab = 3,
                onTabSelected = onTabSelected,
                onAddTaskClick = onAddTaskClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.PaddingLarge),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Header (Title + Profile Avatar Button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.settings_title),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                UserAvatar(
                    avatarUrl = avatarUrl,
                    initials = avatarInitials,
                    size = 44.dp,
                    fontSize = 15.sp,
                    modifier = Modifier.clickable { onProfileClick() }
                )
            }

            // Section 1: GIAO DIỆN
            Text(
                text = stringResource(R.string.settings_section_interface),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Theme Mode Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val modes = listOf(
                            stringResource(R.string.settings_theme_light),
                            stringResource(R.string.settings_theme_dark),
                            stringResource(R.string.settings_theme_auto)
                        )
                        modes.forEachIndexed { index, modeName ->
                            val isSelected = themeMode == index
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onThemeModeChange(index) },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(84.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (index == 1) Color.Black
                                            else if (index == 2) Color(0xFFE9E9EE)
                                            else Color(0xFFF5F5F7)
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFEDEDF0),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .padding(10.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .width(45.dp)
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (index == 1) Color(0xFF48484A) else Color(0xFFD1D1D6))
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(18.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (index == 1) Color(0xFF2C2C2E) else PureWhite)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(18.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (index == 1) Color(0xFF2C2C2E) else PureWhite)
                                        )
                                    }
                                }
                                Text(
                                    text = modeName,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else TextMuted
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFEDEDF0), thickness = 1.dp)

                    // Primary Color Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings_primary_color),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val colorLabels = listOf(
                                stringResource(R.string.cd_color_blue),
                                stringResource(R.string.cd_color_green),
                                stringResource(R.string.cd_color_orange),
                                stringResource(R.string.cd_color_purple)
                            )
                            PrimaryColors.forEachIndexed { index, color ->
                                val isSelected = primaryColorIndex == index
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .clickable { onPrimaryColorChange(index) }
                                        .then(
                                            if (isSelected) {
                                                Modifier.border(2.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                            } else Modifier
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = colorLabels.getOrElse(index) { "" },
                                            tint = PureWhite,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFEDEDF0), thickness = 1.dp)

                    // Font Size Controller
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.settings_font_size),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = when {
                                    fontScale < 0.95f -> stringResource(R.string.settings_font_size_small)
                                    fontScale > 1.05f -> stringResource(R.string.settings_font_size_large)
                                    else -> stringResource(R.string.settings_font_size_standard)
                                },
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val options = listOf(
                                0.85f to stringResource(R.string.settings_font_size_small),
                                1.0f to stringResource(R.string.settings_font_size_standard),
                                1.15f to stringResource(R.string.settings_font_size_large)
                            )
                            options.forEach { (scale, label) ->
                                val isSelected = when (scale) {
                                    0.85f -> fontScale < 0.95f
                                    1.15f -> fontScale > 1.05f
                                    else -> fontScale in 0.95f..1.05f
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else Color.Transparent
                                        )
                                        .clickable { onFontScaleChange(scale) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) PureWhite else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: THÔNG BÁO
            Text(
                text = stringResource(R.string.settings_section_notification),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Item 1: Nhắc việc
                    SettingSwitchRow(
                        title = stringResource(R.string.settings_reminder_title),
                        subtitle = stringResource(R.string.settings_reminder_subtitle),
                        checked = isReminderEnabled,
                        onCheckedChange = onReminderToggle
                    )

                    HorizontalDivider(color = Color(0xFFEDEDF0), thickness = 1.dp)

                    // Item 2: Âm thanh
                    SettingSwitchRow(
                        title = stringResource(R.string.settings_sound_title),
                        subtitle = stringResource(R.string.settings_sound_subtitle),
                        checked = isSoundEnabled,
                        onCheckedChange = onSoundToggle
                    )

                    HorizontalDivider(color = Color(0xFFEDEDF0), thickness = 1.dp)

                    // Item 3: Tổng kết cuối ngày
                    SettingSwitchRow(
                        title = stringResource(R.string.settings_summary_title),
                        subtitle = stringResource(R.string.settings_summary_subtitle),
                        checked = isSummaryEnabled,
                        onCheckedChange = onSummaryToggle
                    )
                }
            }

            // Section 3: CHUNG
            Text(
                text = stringResource(R.string.settings_section_general),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            var showLanguageDialog by remember { mutableStateOf(false) }

            val languageDisplayValue = when (languageCode) {
                "en" -> stringResource(R.string.language_english)
                else -> stringResource(R.string.language_vietnamese)
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingValueRow(
                        title = stringResource(R.string.settings_language_title),
                        value = languageDisplayValue,
                        onClick = { showLanguageDialog = true }
                    )

                    HorizontalDivider(color = Color(0xFFEDEDF0), thickness = 1.dp)

                    SettingValueRow(
                        title = stringResource(R.string.settings_about_title),
                        value = stringResource(R.string.app_version)
                    )
                }
            }

            if (showLanguageDialog) {
                LanguageSelectionDialog(
                    currentLanguage = languageCode,
                    onLanguageSelected = { code ->
                        onLanguageChange(code)
                        showLanguageDialog = false
                    },
                    onDismiss = { showLanguageDialog = false }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextMuted
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PureWhite,
                checkedTrackColor = Color(0xFF34C759),
                uncheckedThumbColor = PureWhite,
                uncheckedTrackColor = BorderGray
            )
        )
    }
}

@Composable
private fun SettingValueRow(
    title: String,
    value: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = value,
                fontSize = 15.sp,
                color = TextMuted
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = BorderGray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val languages = listOf(
        "vi" to stringResource(R.string.language_vietnamese),
        "en" to stringResource(R.string.language_english)
    )

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.settings_language_dialog_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                languages.forEach { (code, label) ->
                    val isSelected = currentLanguage == code
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .selectable(
                                selected = isSelected,
                                onClick = { onLanguageSelected(code) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        RadioButton(
                            selected = isSelected,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.action_cancel),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Preview(name = "Language Dialog Light", showBackground = true)
@Preview(name = "Language Dialog Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LanguageSelectionDialogPreview() {
    UTHTodoTheme {
        LanguageSelectionDialog(
            currentLanguage = "vi",
            onLanguageSelected = {},
            onDismiss = {}
        )
    }
}

@Preview(name = "Settings Screen Light", showBackground = true)
@Preview(name = "Settings Screen Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreenPreview() {
    UTHTodoTheme {
        SettingsScreen()
    }
}
