package com.example.uthtodo.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.R
import com.example.uthtodo.ui.components.UserAvatar
import com.example.uthtodo.ui.theme.BorderGray
import com.example.uthtodo.ui.theme.Dimens
import com.example.uthtodo.ui.theme.PureWhite
import com.example.uthtodo.ui.theme.TextMuted
import com.example.uthtodo.ui.theme.UTHTodoTheme
import java.util.Calendar

data class ProfileDataModel(
    val userName: String = "UTH",
    val userEmail: String = "uth.guest@uth.edu.vn",
    val avatarInitials: String = "UTH",
    val avatarUrl: String? = null,
    val completedCount: Int = 0,
    val streakDays: Int = 0,
    val onTimeRate: Int = 0,
    val isGoogleLinked: Boolean = false,
    val weeklyCompletedCount: Int = 0,
    val weeklyTotalCount: Int = 0,
    val weeklyDailyCompletions: List<Int> = emptyList()
)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    model: ProfileDataModel = ProfileDataModel(),
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ProfileTopBar(
                onBackClick = onBackClick,
                onEditClick = onEditClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.PaddingLarge),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // User Info Header Card
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Avatar with Camera Badge Button
                Box {
                    UserAvatar(
                        avatarUrl = model.avatarUrl,
                        initials = model.avatarInitials,
                        size = 88.dp,
                        fontSize = 32.sp
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.BottomEnd)
                            .clickable { onAvatarClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = stringResource(R.string.profile_cd_change_avatar),
                            tint = PureWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = model.userName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = model.userEmail,
                    fontSize = 14.sp,
                    color = TextMuted
                )

                // Account Badge: Google Linked or Guest
                if (model.isGoogleLinked) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFDDF5EA))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF0F7A4F),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = stringResource(R.string.profile_google_linked),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F7A4F)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = stringResource(R.string.profile_guest_badge),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Stat Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Completed
                StatCard(
                    numberText = "${model.completedCount}",
                    labelText = stringResource(R.string.profile_stat_completed),
                    modifier = Modifier.weight(1f)
                )

                // Card 2: Streak
                StatCard(
                    numberText = stringResource(R.string.profile_stat_streak_days, model.streakDays),
                    labelText = stringResource(R.string.profile_stat_streak),
                    modifier = Modifier.weight(1f)
                )

                // Card 3: On time rate
                StatCard(
                    numberText = "${model.onTimeRate}%",
                    labelText = stringResource(R.string.profile_stat_ontime),
                    modifier = Modifier.weight(1f)
                )
            }

            // Weekly Progress Bar Chart Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.profile_this_week),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(
                                R.string.home_progress_format,
                                model.weeklyCompletedCount,
                                model.weeklyTotalCount
                            ),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (model.weeklyTotalCount == 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.profile_weekly_empty),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // Dynamic Bar Chart Row
                        val cal = Calendar.getInstance()
                        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                        val currentDayIndex = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY

                        val dayLabels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                        val maxDayCount = (model.weeklyDailyCompletions.maxOrNull() ?: 1).coerceAtLeast(1)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(96.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            dayLabels.forEachIndexed { index, dayLabel ->
                                val count = model.weeklyDailyCompletions.getOrElse(index) { 0 }
                                val isHighlighted = index == currentDayIndex
                                val barHeight = if (count == 0) 8.dp else (16.dp + (56.dp * count / maxDayCount))

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(26.dp)
                                            .height(barHeight)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (isHighlighted) MaterialTheme.colorScheme.primary
                                                else if (count > 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                    )
                                    Text(
                                        text = dayLabel,
                                        fontSize = 11.sp,
                                        fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isHighlighted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Sync & Privacy Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Sync Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Đồng bộ",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.profile_sync_title),
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = stringResource(R.string.time_just_now),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

                    // Privacy Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPrivacyClick() }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = stringResource(R.string.profile_privacy_title),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.profile_privacy_title),
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Xem",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Logout Button
            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = Color(0xFFC4321F)
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = stringResource(R.string.profile_btn_logout),
                        tint = Color(0xFFC4321F),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(R.string.profile_btn_logout),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFC4321F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileTopBar(
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.clickable { onBackClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = stringResource(R.string.nav_settings),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(R.string.nav_settings),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = stringResource(R.string.profile_title),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        TextButton(onClick = onEditClick) {
            Text(
                text = stringResource(R.string.action_edit),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun StatCard(
    numberText: String,
    labelText: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(12.dp, 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = numberText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = labelText,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(name = "Profile Screen Populated - Light", showBackground = true)
@Preview(name = "Profile Screen Populated - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenPreview() {
    UTHTodoTheme {
        ProfileScreen(
            model = ProfileDataModel(
                userName = "Nguyễn Văn A",
                userEmail = "vana@uth.edu.vn",
                avatarInitials = "NA",
                completedCount = 15,
                streakDays = 3,
                onTimeRate = 80,
                isGoogleLinked = true,
                weeklyCompletedCount = 6,
                weeklyTotalCount = 8,
                weeklyDailyCompletions = listOf(2, 1, 0, 3, 0, 0, 0)
            )
        )
    }
}

@Preview(name = "Profile Screen Empty Guest - Light", showBackground = true)
@Preview(name = "Profile Screen Empty Guest - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenGuestEmptyPreview() {
    UTHTodoTheme {
        ProfileScreen(
            model = ProfileDataModel(
                userName = "UTH Guest",
                userEmail = "Dùng thử không cần tài khoản",
                avatarInitials = "UG",
                isGoogleLinked = false,
                weeklyCompletedCount = 0,
                weeklyTotalCount = 0
            )
        )
    }
}
