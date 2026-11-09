package com.example.uthtodo.ui.screens

import android.content.res.Configuration
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.uthtodo.R
import com.example.uthtodo.ui.components.asLocalizedCategory
import com.example.uthtodo.ui.components.asLocalizedTime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.ui.components.EmptyStateView
import com.example.uthtodo.ui.theme.BorderGray
import com.example.uthtodo.ui.theme.Dimens
import com.example.uthtodo.ui.theme.GrayBorder
import com.example.uthtodo.ui.theme.OrangeTagBg
import com.example.uthtodo.ui.theme.OrangeTagText
import com.example.uthtodo.ui.theme.PureWhite
import com.example.uthtodo.ui.theme.TextDark
import com.example.uthtodo.ui.theme.TextMuted
import com.example.uthtodo.ui.theme.UTHTodoTheme

data class TaskItemModel(
    val id: String,
    val title: String,
    val categoryName: String,
    val timeText: String,
    val isChecked: Boolean = false,
    val isPrimaryCategory: Boolean = true,
    val isUrgent: Boolean = false
)

data class TaskListScreenModel(
    val uncompletedCount: Int = 0,
    val pendingTasks: List<TaskItemModel> = emptyList(),
    val completedTasks: List<TaskItemModel> = emptyList()
)

@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    model: TaskListScreenModel = TaskListScreenModel(),
    onTaskToggle: (String) -> Unit = {},
    onTaskClick: (String) -> Unit = {},
    onTaskDelete: (String) -> Unit = {},
    onTabSelected: (Int) -> Unit = {},
    onAddTaskClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableIntStateOf(0) } // 0: Tất cả, 1: Học tập, 2: Cuộc sống

    val filterByCategory: (TaskItemModel) -> Boolean = { task ->
        when (selectedCategoryFilter) {
            1 -> task.categoryName == "Học tập"
            2 -> task.categoryName == "Cuộc sống"
            else -> true
        }
    }
    val filterBySearch: (TaskItemModel) -> Boolean = { task ->
        searchQuery.isBlank() || task.title.contains(searchQuery, ignoreCase = true)
    }

    val displayPending = model.pendingTasks.filter { filterByCategory(it) && filterBySearch(it) }
    val displayCompleted = model.completedTasks.filter { filterByCategory(it) && filterBySearch(it) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            HomeBottomNavigation(
                selectedTab = 1,
                onTabSelected = onTabSelected,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Header (Title + Uncompleted count)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.task_list_title),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.task_list_uncompleted_format, model.uncompletedCount),
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.task_list_search_placeholder),
                            color = TextMuted,
                            fontSize = 15.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.task_list_cd_search),
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = BorderGray
                    ),
                    singleLine = true
                )
            }

            // Category Filter Pills
            item {
                val filterLabels = listOf(
                    stringResource(R.string.task_list_filter_all),
                    stringResource(R.string.task_list_filter_study),
                    stringResource(R.string.task_list_filter_life)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterLabels.forEachIndexed { index, label ->
                        val isSelected = selectedCategoryFilter == index
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { selectedCategoryFilter = index },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else BorderGray
                            )
                        ) {
                            Text(
                                text = label,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) PureWhite else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Empty State
            if (displayPending.isEmpty() && displayCompleted.isEmpty()) {
                item {
                    EmptyStateView(
                        title = if (searchQuery.isNotBlank()) stringResource(R.string.task_list_empty_search_title) else stringResource(R.string.task_list_empty_title),
                        subtitle = if (searchQuery.isNotBlank()) stringResource(R.string.task_list_empty_search_subtitle) else stringResource(R.string.task_list_empty_subtitle)
                    )
                }
            } else {
                // Section 1: CẦN LÀM
                if (displayPending.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.task_list_section_pending, displayPending.size),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                    }

                    items(displayPending, key = { "pending_${it.id}" }) { task ->
                        TaskCardItem(
                            task = task,
                            onToggle = { onTaskToggle(task.id) },
                            onClick = { onTaskClick(task.id) },
                            onDelete = { onTaskDelete(task.id) }
                        )
                    }
                }

                // Section 2: ĐÃ XONG
                if (displayCompleted.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.task_list_section_completed, displayCompleted.size),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                    }

                    items(displayCompleted, key = { "completed_${it.id}" }) { task ->
                        TaskCardItem(
                            task = task,
                            onToggle = { onTaskToggle(task.id) },
                            onClick = { onTaskClick(task.id) },
                            onDelete = { onTaskDelete(task.id) }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun TaskCardItem(
    task: TaskItemModel,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Native Checkbox
            Checkbox(
                checked = task.isChecked,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = GrayBorder
                ),
                modifier = Modifier.semantics {
                    contentDescription = if (task.isChecked) {
                        "Đã hoàn thành công việc ${task.title}"
                    } else {
                        "Chưa hoàn thành công việc ${task.title}"
                    }
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (task.isChecked) TextMuted else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isChecked) TextDecoration.LineThrough else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (task.isPrimaryCategory) MaterialTheme.colorScheme.primaryContainer else OrangeTagBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.categoryName.asLocalizedCategory(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (task.isPrimaryCategory) MaterialTheme.colorScheme.primary else OrangeTagText
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = task.timeText.asLocalizedTime(),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            // Urgent Priority Badge
            if (task.isUrgent && !task.isChecked) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFDE7E7))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = stringResource(R.string.task_list_tag_urgent),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFB42318)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            // Delete Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.action_delete),
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(name = "Task List Screen Light", showBackground = true)
@Preview(name = "Task List Screen Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TaskListScreenPreview() {
    UTHTodoTheme {
        TaskListScreen()
    }
}
