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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import android.content.res.Configuration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.uthtodo.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.model.CategorySummaryModel
import com.example.uthtodo.model.HomeScreenModel
import com.example.uthtodo.model.HomeTaskItem
import com.example.uthtodo.ui.components.EmptyStateView
import com.example.uthtodo.ui.components.UserAvatar
import com.example.uthtodo.ui.theme.BorderGray
import com.example.uthtodo.ui.theme.Dimens
import com.example.uthtodo.ui.theme.IndicatorInactive
import com.example.uthtodo.ui.theme.LightSoftBlue
import com.example.uthtodo.ui.theme.OrangeTagBg
import com.example.uthtodo.ui.theme.OrangeTagText
import com.example.uthtodo.ui.theme.PrimaryBlue
import com.example.uthtodo.ui.theme.PureWhite
import com.example.uthtodo.ui.theme.SoftBlueBg
import com.example.uthtodo.ui.theme.TextDark
import com.example.uthtodo.ui.theme.TextMuted
import com.example.uthtodo.ui.theme.TextSecondary
import com.example.uthtodo.ui.theme.UTHTodoTheme
import com.example.uthtodo.ui.theme.WhiteAlpha28
import com.example.uthtodo.ui.theme.WhiteAlpha90

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    model: HomeScreenModel = HomeScreenModel(),
    unreadNotificationCount: Int = 0,
    onNotificationClick: () -> Unit = {},
    onTaskToggle: (String) -> Unit = {},
    onTaskClick: (String) -> Unit = {},
    onAddTaskClick: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {},
    onNavigateToTaskList: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            HomeBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    onTabSelected(tab)
                },
                onAddTaskClick = onAddTaskClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = Dimens.PaddingLarge),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Header (Avatar + Greeting + Bell)
            item {
                HomeHeader(
                    userName = model.userHeader.userName,
                    dateText = model.userHeader.dateText,
                    avatarInitials = model.userHeader.avatarInitials,
                    avatarUrl = model.userHeader.avatarUrl,
                    unreadNotificationCount = unreadNotificationCount,
                    onNotificationClick = onNotificationClick
                )
            }

            // Today Progress Banner Card
            item {
                TodayProgressCard(
                    completedCount = model.progress.completedCount,
                    totalCount = model.progress.totalCount,
                    percentage = model.progress.percentage,
                    motivationText = model.progress.motivationText
                )
            }

            // Category Cards Row
            item {
                CategoryCardsRow(categories = model.categories)
            }

            // Today Tasks Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.home_today_tasks_title),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.action_see_all),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryBlue,
                        modifier = Modifier.clickable { onNavigateToTaskList() }
                    )
                }
            }

            // Tasks List
            if (model.todayTasks.isEmpty()) {
                item {
                    EmptyStateView(
                        title = stringResource(R.string.home_empty_title),
                        subtitle = stringResource(R.string.home_empty_subtitle)
                    )
                }
            } else {
                items(model.todayTasks, key = { it.id }) { task ->
                    HomeTaskCard(
                        task = task,
                        onToggle = { onTaskToggle(task.id) },
                        onClick = { onTaskClick(task.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    dateText: String,
    avatarInitials: String,
    avatarUrl: String? = null,
    unreadNotificationCount: Int = 0,
    onNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Profile Avatar
            UserAvatar(
                avatarUrl = avatarUrl,
                initials = avatarInitials,
                size = 48.dp,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = dateText,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Text(
                    text = userName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Notification Bell Icon with Red Dot
        Box(
            modifier = Modifier.clickable(onClick = onNotificationClick)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = WhiteAlpha28
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = stringResource(R.string.nav_notifications),
                        tint = PrimaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            if (unreadNotificationCount > 0) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Composable
private fun TodayProgressCard(
    completedCount: Int,
    totalCount: Int,
    percentage: Int,
    motivationText: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.home_today_progress_title),
                    fontSize = 13.sp,
                    color = PureWhite,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.home_progress_format, completedCount, totalCount),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = motivationText,
                    fontSize = 14.sp,
                    color = PureWhite,
                    fontWeight = FontWeight.Medium
                )
            }

            // Circular Progress % Badge
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier.size(64.dp),
                    color = SoftBlueBg,
                    trackColor = PureWhite,
                    strokeWidth = 6.dp
                )
                Text(
                    text = "$percentage%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            }
        }
    }
}

@Composable
private fun CategoryCardsRow(
    categories: List<CategorySummaryModel>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        categories.forEach { cat ->
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(115.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (cat.isPrimary) LightSoftBlue else OrangeTagBg
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (cat.isPrimary) PrimaryBlue else OrangeTagText),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (cat.isPrimary) Icons.Default.School else Icons.Default.HomeWork,
                            contentDescription = cat.name,
                            tint = PureWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = cat.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (cat.isPrimary) PrimaryBlue else OrangeTagText
                        )
                        Text(
                            text = stringResource(R.string.home_category_remaining_format, cat.remainingCount),
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTaskCard(
    task: HomeTaskItem,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Native Checkbox
            Checkbox(
                checked = task.isChecked,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = PrimaryBlue,
                    uncheckedColor = IndicatorInactive
                ),
                modifier = Modifier.semantics {
                    contentDescription = if (task.isChecked) {
                        "Đã hoàn thành công việc ${task.title}"
                    } else {
                        "Chưa hoàn thành công việc ${task.title}"
                    }
                }
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.isChecked) TextSecondary else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isChecked) TextDecoration.LineThrough else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (task.isPrimaryCategory) LightSoftBlue else OrangeTagBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.categoryName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (task.isPrimaryCategory) PrimaryBlue else OrangeTagText
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = task.timeText,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun HomeBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onAddTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Top border line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline)
                .align(Alignment.TopCenter)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 0: Trang chủ
                NavigationTabItem(
                    selected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = if (selectedTab == 0) Icons.Default.Home else Icons.Outlined.Home,
                    label = stringResource(R.string.nav_home),
                    modifier = Modifier.weight(1f)
                )

                // Tab 1: Danh sách
                NavigationTabItem(
                    selected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    icon = if (selectedTab == 1) Icons.AutoMirrored.Filled.FormatListBulleted else Icons.AutoMirrored.Outlined.FormatListBulleted,
                    label = stringResource(R.string.nav_task_list),
                    modifier = Modifier.weight(1f)
                )

                // Center Spacer for FAB
                Spacer(modifier = Modifier.weight(1f))

                // Tab 2: Thông báo
                NavigationTabItem(
                    selected = selectedTab == 2,
                    onClick = { onTabSelected(2) },
                    icon = if (selectedTab == 2) Icons.Default.Notifications else Icons.Outlined.Notifications,
                    label = stringResource(R.string.nav_notifications),
                    modifier = Modifier.weight(1f)
                )

                // Tab 3: Cài đặt
                NavigationTabItem(
                    selected = selectedTab == 3,
                    onClick = { onTabSelected(3) },
                    icon = if (selectedTab == 3) Icons.Default.Settings else Icons.Outlined.Settings,
                    label = stringResource(R.string.nav_settings),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Floating Action Button in the center, popping up above the navigation bar
        FloatingActionButton(
            onClick = onAddTaskClick,
            shape = CircleShape,
            containerColor = PrimaryBlue,
            contentColor = PureWhite,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            ),
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.action_add_task),
                modifier = Modifier.size(26.dp),
                tint = PureWhite
            )
        }
    }
}

@Composable
private fun NavigationTabItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) PrimaryBlue else TextMuted,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) PrimaryBlue else TextMuted
        )
    }
}

@Preview(name = "Home Screen Light", showBackground = true)
@Preview(name = "Home Screen Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
    UTHTodoTheme {
        HomeScreen()
    }
}

@Preview(name = "Home Bottom Navigation Light", showBackground = true)
@Preview(name = "Home Bottom Navigation Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeBottomNavigationPreview() {
    UTHTodoTheme {
        HomeBottomNavigation(
            selectedTab = 0,
            onTabSelected = {},
            onAddTaskClick = {}
        )
    }
}
