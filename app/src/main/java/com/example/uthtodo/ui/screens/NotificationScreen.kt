package com.example.uthtodo.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.R
import com.example.uthtodo.data.model.AppNotification
import com.example.uthtodo.ui.components.EmptyStateView
import com.example.uthtodo.ui.theme.BorderGray
import com.example.uthtodo.ui.theme.Dimens
import com.example.uthtodo.ui.theme.LightSoftBlue
import com.example.uthtodo.ui.theme.PrimaryBlue
import com.example.uthtodo.ui.theme.TextMuted
import com.example.uthtodo.ui.theme.UTHTodoTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    notifications: List<AppNotification> = emptyList(),
    unreadCount: Int = 0,
    onNotificationClick: (AppNotification) -> Unit = {},
    onMarkAllReadClick: () -> Unit = {},
    onDeleteNotification: (String) -> Unit = {},
    onTabSelected: (Int) -> Unit = {},
    onAddTaskClick: () -> Unit = {}
) {
    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0: Tất cả, 1: Nhắc việc, 2: Hệ thống
    val filterLabels = listOf(
        stringResource(R.string.task_list_filter_all),
        stringResource(R.string.notification_filter_reminder),
        stringResource(R.string.notification_filter_system)
    )

    val filteredNotifications = remember(notifications, selectedFilterIndex) {
        when (selectedFilterIndex) {
            1 -> notifications.filter { it.type == "REMINDER" || it.type == "OVERDUE" }
            2 -> notifications.filter { it.type == "SYSTEM" || it.type == "DAILY_SUMMARY" }
            else -> notifications
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            HomeBottomNavigation(
                selectedTab = 2,
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
        ) {
            // Top Bar
            NotificationTopBar(
                unreadCount = unreadCount,
                onMarkAllReadClick = onMarkAllReadClick
            )

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingLarge, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterLabels.forEachIndexed { index, label ->
                    val isSelected = selectedFilterIndex == index
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilterIndex = index },
                        label = {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LightSoftBlue,
                            selectedLabelColor = PrimaryBlue,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = TextMuted
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) PrimaryBlue else BorderGray
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Notification List or Empty State
            if (filteredNotifications.isEmpty()) {
                val (emptyIcon, emptyTitle, emptySubtitle) = when (selectedFilterIndex) {
                    1 -> Triple(
                        Icons.Outlined.NotificationsActive,
                        stringResource(R.string.notification_empty_reminder_title),
                        stringResource(R.string.notification_empty_reminder_subtitle)
                    )
                    2 -> Triple(
                        Icons.Outlined.Info,
                        stringResource(R.string.notification_empty_system_title),
                        stringResource(R.string.notification_empty_system_subtitle)
                    )
                    else -> Triple(
                        Icons.Outlined.Notifications,
                        stringResource(R.string.notification_empty_title),
                        stringResource(R.string.notification_empty_subtitle)
                    )
                }
                EmptyStateView(
                    icon = emptyIcon,
                    title = emptyTitle,
                    subtitle = emptySubtitle,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimens.PaddingLarge)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = Dimens.PaddingLarge,
                        vertical = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = filteredNotifications,
                        key = { it.id.ifEmpty { it.hashCode().toString() } }
                    ) { item ->
                        NotificationCard(
                            notification = item,
                            onClick = { onNotificationClick(item) },
                            onDelete = { onDeleteNotification(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationTopBar(
    unreadCount: Int,
    onMarkAllReadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingLarge, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = stringResource(R.string.notification_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (unreadCount > 0) {
                Text(
                    text = stringResource(R.string.notification_unread_format, unreadCount),
                    fontSize = 13.sp,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (unreadCount > 0) {
            TextButton(onClick = onMarkAllReadClick) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.notification_mark_all_read),
                        fontSize = 13.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: AppNotification,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val (icon, iconBg, iconTint) = when (notification.type) {
        "OVERDUE" -> Triple(
            Icons.Outlined.Warning,
            if (isDark) Color(0xFF3E1A1A) else Color(0xFFFFEBEE),
            if (isDark) Color(0xFFFF6B6B) else Color(0xFFD32F2F)
        )
        "SYSTEM" -> Triple(
            Icons.Outlined.Info,
            if (isDark) Color(0xFF2E1A47) else Color(0xFFF3E8FF),
            if (isDark) Color(0xFFC084FC) else Color(0xFF7C3AED)
        )
        else -> Triple(
            Icons.Outlined.NotificationsActive,
            if (isDark) MaterialTheme.colorScheme.surfaceVariant else LightSoftBlue,
            PrimaryBlue
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (notification.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        border = androidx.compose.foundation.BorderStroke(
            width = if (notification.isRead) 1.dp else 1.5.dp,
            color = if (notification.isRead) BorderGray else PrimaryBlue.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Body
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 15.sp,
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Unread blue dot indicator
                    if (!notification.isRead) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue)
                        )
                    }
                }

                if (notification.message.isNotBlank()) {
                    Text(
                        text = notification.message,
                        fontSize = 13.sp,
                        color = if (notification.isRead) TextMuted else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatNotificationTime(notification.createdAt),
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.notification_cd_delete),
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun formatNotificationTime(date: Date?): String {
    if (date == null) return stringResource(R.string.time_just_now)
    val diff = System.currentTimeMillis() - date.time
    val minutes = diff / (60 * 1000)
    val hours = diff / (60 * 60 * 1000)
    val days = diff / (24 * 60 * 60 * 1000)

    return when {
        minutes < 1 -> stringResource(R.string.time_just_now)
        minutes < 60 -> stringResource(R.string.time_minutes_ago, minutes)
        hours < 24 -> stringResource(R.string.time_hours_ago, hours)
        days == 1L -> stringResource(R.string.time_yesterday)
        days < 7 -> stringResource(R.string.time_days_ago, days)
        else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }
}

@Preview(name = "Notification Screen Light", showBackground = true)
@Preview(name = "Notification Screen Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NotificationScreenPreview() {
    UTHTodoTheme {
        NotificationScreen(
            unreadCount = 2,
            notifications = listOf(
                AppNotification(
                    id = "1",
                    title = "Nộp bài tập Toán giải tích",
                    message = "Còn 30 phút nữa là đến hạn chót bài tập tuần 5.",
                    type = "REMINDER",
                    isRead = false,
                    createdAt = Date()
                ),
                AppNotification(
                    id = "2",
                    title = "Ôn thi tiếng Anh",
                    message = "Đã quá hạn 2 giờ. Hãy kiểm tra lại danh sách công việc.",
                    type = "OVERDUE",
                    isRead = false,
                    createdAt = Date(System.currentTimeMillis() - 7200000)
                ),
                AppNotification(
                    id = "3",
                    title = "Hệ thống đã đồng bộ",
                    message = "Dữ liệu công việc của bạn đã được sao lưu lên đám mây.",
                    type = "SYSTEM",
                    isRead = true,
                    createdAt = Date(System.currentTimeMillis() - 86400000)
                )
            )
        )
    }
}

@Preview(name = "Notification Screen Empty Light", showBackground = true)
@Preview(name = "Notification Screen Empty Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NotificationScreenEmptyPreview() {
    UTHTodoTheme {
        NotificationScreen(
            unreadCount = 0,
            notifications = emptyList()
        )
    }
}
