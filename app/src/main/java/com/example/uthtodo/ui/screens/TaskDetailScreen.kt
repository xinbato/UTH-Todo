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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.uthtodo.R
import com.example.uthtodo.ui.components.asLocalizedCategory
import com.example.uthtodo.ui.components.asLocalizedPriority
import com.example.uthtodo.ui.components.asLocalizedTime
import com.example.uthtodo.data.model.Subtask
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.ui.theme.BorderGray
import com.example.uthtodo.ui.theme.Dimens
import com.example.uthtodo.ui.theme.GrayBorder
import com.example.uthtodo.ui.theme.OrangeTagBg
import com.example.uthtodo.ui.theme.OrangeTagText
import com.example.uthtodo.ui.theme.PureWhite
import com.example.uthtodo.ui.theme.TextMuted
import com.example.uthtodo.ui.theme.UTHTodoTheme

data class TaskDetailDataModel(
    val id: String = "",
    val title: String = "Chi tiết công việc",
    val categoryName: String = "Học tập",
    val priorityText: String = "Vừa",
    val dueDateText: String = "Hôm nay",
    val reminderText: String = "Trước 15 phút",
    val repeatText: String = "Không",
    val noteText: String = "",
    val isDone: Boolean = false,
    val subtasks: List<Subtask> = emptyList()
)

@Composable
fun TaskDetailScreen(
    modifier: Modifier = Modifier,
    model: TaskDetailDataModel = TaskDetailDataModel(),
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onCompleteToggle: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onAddSubtask: (String) -> Unit = {},
    onSubtaskToggle: (subtaskId: String, isCompleted: Boolean) -> Unit = { _, _ -> },
    onSubtaskDelete: (subtaskId: String) -> Unit = {}
) {
    var newSubtaskText by remember { mutableStateOf("") }
    val doneCount = model.subtasks.count { it.isCompleted }
    val totalSubtasks = model.subtasks.size
    val progressFraction = if (totalSubtasks > 0) doneCount.toFloat() / totalSubtasks else 0f

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TaskDetailTopBar(
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

            // Category & Priority Tags
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val isPrimary = model.categoryName == "Học tập"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isPrimary) MaterialTheme.colorScheme.primaryContainer else OrangeTagBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = model.categoryName.asLocalizedCategory(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isPrimary) MaterialTheme.colorScheme.primary else OrangeTagText
                    )
                }

                if (model.priorityText.isNotBlank()) {
                    val isUrgent = model.priorityText == "Cao"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isUrgent) Color(0xFFFDE7E7) else Color(0xFFE9E9EE))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.task_detail_priority_prefix, model.priorityText.asLocalizedPriority()),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isUrgent) Color(0xFFB42318) else Color(0xFF5C5C62)
                        )
                    }
                }
            }

            // Task Title
            Text(
                text = model.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 34.sp
            )

            // Meta Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                val displayReminder = when (model.reminderText) {
                    "Trước 15 phút" -> stringResource(R.string.add_task_reminder_subtitle)
                    "Không có" -> stringResource(R.string.task_detail_reminder_none)
                    else -> model.reminderText
                }
                val displayRepeat = when (model.repeatText) {
                    "Không" -> stringResource(R.string.task_detail_repeat_none)
                    else -> model.repeatText
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    // Due Date
                    DetailMetaRow(
                        icon = Icons.Outlined.CalendarToday,
                        label = stringResource(R.string.task_detail_due_date_label),
                        value = model.dueDateText.asLocalizedTime()
                    )

                    HorizontalDivider(color = Color(0xFFEDEDF0), thickness = 1.dp)

                    // Reminder
                    DetailMetaRow(
                        icon = Icons.Outlined.Notifications,
                        label = stringResource(R.string.task_detail_reminder_label),
                        value = displayReminder
                    )

                    HorizontalDivider(color = Color(0xFFEDEDF0), thickness = 1.dp)

                    // Repeat
                    DetailMetaRow(
                        icon = Icons.Outlined.Repeat,
                        label = stringResource(R.string.task_detail_repeat_label),
                        value = displayRepeat
                    )
                }
            }

            // Note Section
            if (model.noteText.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.add_task_note_label),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = model.noteText,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(16.dp, 14.dp)
                        )
                    }
                }
            }

            // Subtasks Section (Việc nhỏ)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.task_detail_subtasks_title),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted
                    )
                    Text(
                        text = "$doneCount/$totalSubtasks",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted
                    )
                }

                if (totalSubtasks > 0) {
                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color(0xFFE4E4EA)
                    )
                }

                // Subtasks List Container Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (model.subtasks.isEmpty()) {
                            Text(
                                text = "Chưa có việc nhỏ nào. Thêm bước thực hiện bên dưới.",
                                fontSize = 13.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                            )
                            HorizontalDivider(color = Color(0xFFEDEDF0), thickness = 1.dp)
                        } else {
                            model.subtasks.forEach { sub ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Subtask Checkbox
                                    Checkbox(
                                        checked = sub.isCompleted,
                                        onCheckedChange = { isChecked ->
                                            onSubtaskToggle(sub.id, isChecked)
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.primary,
                                            uncheckedColor = GrayBorder
                                        ),
                                        modifier = Modifier.semantics {
                                            contentDescription = if (sub.isCompleted) {
                                                "Việc nhỏ đã xong: ${sub.title}"
                                            } else {
                                                "Việc nhỏ chưa xong: ${sub.title}"
                                            }
                                        }
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = sub.title,
                                        fontSize = 15.sp,
                                        color = if (sub.isCompleted) TextMuted else MaterialTheme.colorScheme.onSurface,
                                        textDecoration = if (sub.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                        modifier = Modifier.weight(1f)
                                    )

                                    IconButton(
                                        onClick = { onSubtaskDelete(sub.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Delete,
                                            contentDescription = "Xóa việc nhỏ",
                                            tint = TextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                HorizontalDivider(color = Color(0xFFEDEDF0), thickness = 1.dp)
                            }
                        }

                        // Add Subtask Input Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.task_detail_subtask_add_cd),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            BasicTextField(
                                value = newSubtaskText,
                                onValueChange = { newSubtaskText = it },
                                modifier = Modifier.weight(1f),
                                textStyle = TextStyle(
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                decorationBox = { innerTextField ->
                                    if (newSubtaskText.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.task_detail_subtask_input_placeholder),
                                            fontSize = 15.sp,
                                            color = TextMuted
                                        )
                                    }
                                    innerTextField()
                                },
                                singleLine = true
                            )
                            TextButton(
                                onClick = {
                                    if (newSubtaskText.isNotBlank()) {
                                        onAddSubtask(newSubtaskText.trim())
                                        newSubtaskText = ""
                                    }
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.action_add),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (newSubtaskText.isNotBlank()) MaterialTheme.colorScheme.primary else Color(0xFF8A8A8E)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Complete Action Button
            Button(
                onClick = onCompleteToggle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = PureWhite
                )
            ) {
                Text(
                    text = if (model.isDone) stringResource(R.string.task_detail_btn_mark_incomplete) else stringResource(R.string.task_detail_btn_mark_complete),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Delete Task Text Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDeleteClick() }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.action_delete),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFC4321F)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TaskDetailTopBar(
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
                contentDescription = stringResource(R.string.nav_task_list),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(R.string.nav_task_list),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

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
private fun DetailMetaRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF5C5C62),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 15.sp,
                color = Color(0xFF5C5C62)
            )
        }

        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(name = "Task Detail Screen Light", showBackground = true)
@Preview(name = "Task Detail Screen Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TaskDetailScreenPreview() {
    UTHTodoTheme {
        TaskDetailScreen(
            model = TaskDetailDataModel(
                title = "Thiết kế giao diện UTH Todo",
                categoryName = "Học tập",
                priorityText = "Cao",
                dueDateText = "14:00, 25/11",
                reminderText = "Trước 15 phút",
                noteText = "Hoàn thành các màn hình chính theo thiết kế",
                subtasks = listOf(
                    Subtask(id = "1", title = "Tạo màn hình chi tiết", isCompleted = true),
                    Subtask(id = "2", title = "Kết nối dữ liệu Firestore", isCompleted = false)
                )
            )
        )
    }
}

@Preview(name = "Task Detail Screen Empty Subtasks Light", showBackground = true)
@Preview(name = "Task Detail Screen Empty Subtasks Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TaskDetailScreenEmptySubtasksPreview() {
    UTHTodoTheme {
        TaskDetailScreen(
            model = TaskDetailDataModel(
                title = "Đọc tài liệu Jetpack Compose",
                categoryName = "Học tập",
                priorityText = "Vừa",
                dueDateText = "18:00, Hôm nay",
                reminderText = "Không có",
                subtasks = emptyList()
            )
        )
    }
}
