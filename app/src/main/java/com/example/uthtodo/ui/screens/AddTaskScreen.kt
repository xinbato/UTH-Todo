package com.example.uthtodo.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.example.uthtodo.ui.theme.BorderGray
import com.example.uthtodo.ui.theme.Dimens
import com.example.uthtodo.ui.theme.LightSoftBlue
import com.example.uthtodo.ui.theme.PrimaryBlue
import com.example.uthtodo.ui.theme.PrimaryBlueDark
import com.example.uthtodo.ui.theme.PureWhite
import com.example.uthtodo.ui.theme.SoftBlueBg
import com.example.uthtodo.ui.theme.TextMuted
import com.example.uthtodo.ui.theme.UTHTodoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onCancelClick: () -> Unit = {},
    onSaveClick: (
        title: String,
        note: String,
        category: String,
        priority: String,
        dueDate: Date?,
        hasReminder: Boolean
    ) -> Unit = { _, _, _, _, _, _ -> }
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val calendar = remember {
        Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, 1)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
    val dateFormat = remember { SimpleDateFormat("EEE, dd/MM", Locale.forLanguageTag("vi-VN")) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis
    )
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true
    )

    var selectedDueDate by remember { mutableStateOf<Date?>(calendar.time) }
    var selectedDateText by remember { mutableStateOf(dateFormat.format(calendar.time)) }
    var selectedTimeText by remember { mutableStateOf(timeFormat.format(calendar.time)) }
    var taskTitle by remember { mutableStateOf("") }
    var taskNote by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Học tập") }
    var isReminderEnabled by remember { mutableStateOf(true) }
    var selectedPriority by remember { mutableIntStateOf(1) } // 0: Thấp, 1: Vừa, 2: Cao

    val categories = listOf("Học tập", "Cuộc sống")
    val priorities = listOf("Thấp", "Vừa", "Cao")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AddTaskTopBar(
                isLoading = isLoading,
                isSaveEnabled = taskTitle.isNotBlank(),
                onCancelClick = onCancelClick,
                onSaveClick = {
                    val priorityStr = priorities.getOrElse(selectedPriority) { "Vừa" }
                    onSaveClick(
                        taskTitle,
                        taskNote,
                        selectedCategory,
                        priorityStr,
                        selectedDueDate,
                        isReminderEnabled
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.PaddingLarge),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Card 1: Title & Note Input
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Title Field
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.add_task_title_label),
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box {
                            if (taskTitle.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.add_task_title_placeholder),
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            androidx.compose.foundation.text.BasicTextField(
                                value = taskTitle,
                                onValueChange = { taskTitle = it },
                                singleLine = true,
                                enabled = !isLoading,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

                    // Note Field
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.add_task_note_label),
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box {
                            if (taskNote.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.add_task_note_placeholder),
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            androidx.compose.foundation.text.BasicTextField(
                                value = taskNote,
                                onValueChange = { taskNote = it },
                                enabled = !isLoading,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Section 2: Nhóm (Category Selection)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_task_category_label),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable(enabled = !isLoading) { selectedCategory = cat },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) LightSoftBlue else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) PrimaryBlueDark else MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = cat,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) PrimaryBlueDark else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Card 3: Date, Time, Reminder
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Date Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = stringResource(R.string.add_task_date_label),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.add_task_date_label),
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable(enabled = !isLoading) {
                                showDatePicker = true
                            }
                        ) {
                            Text(
                                text = selectedDateText,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

                    // Time Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = stringResource(R.string.add_task_time_label),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.add_task_time_label),
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable(enabled = !isLoading) {
                                showTimePicker = true
                            }
                        ) {
                            Text(
                                text = selectedTimeText,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

                    // Reminder Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = stringResource(R.string.add_task_reminder_label),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.add_task_reminder_label),
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stringResource(R.string.add_task_reminder_subtitle),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isReminderEnabled,
                            onCheckedChange = { isReminderEnabled = it },
                            enabled = !isLoading,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = Color(0xFF34C759),
                                uncheckedThumbColor = PureWhite,
                                uncheckedTrackColor = BorderGray
                            )
                        )
                    }
                }
            }

            // Section 4: Mức ưu tiên (Priority Segmented Control)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_task_priority_label),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        priorities.forEachIndexed { index, pName ->
                            val isSelected = selectedPriority == index
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .clickable(enabled = !isLoading) { selectedPriority = index },
                                shape = RoundedCornerShape(9.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                                shadowElevation = if (isSelected) 2.dp else 0.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = pName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save Task Button
            Button(
                onClick = {
                    val priorityStr = priorities.getOrElse(selectedPriority) { "Vừa" }
                    onSaveClick(
                        taskTitle,
                        taskNote,
                        selectedCategory,
                        priorityStr,
                        selectedDueDate,
                        isReminderEnabled
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading && taskTitle.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = PureWhite
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = PureWhite,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.add_task_btn_save),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                timeInMillis = millis
                            }
                            calendar.set(Calendar.YEAR, utcCalendar.get(Calendar.YEAR))
                            calendar.set(Calendar.MONTH, utcCalendar.get(Calendar.MONTH))
                            calendar.set(Calendar.DAY_OF_MONTH, utcCalendar.get(Calendar.DAY_OF_MONTH))
                            selectedDateText = dateFormat.format(calendar.time)
                            selectedDueDate = calendar.time
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(text = stringResource(R.string.action_confirm), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        calendar.set(Calendar.MINUTE, timePickerState.minute)
                        selectedTimeText = timeFormat.format(calendar.time)
                        selectedDueDate = calendar.time
                        showTimePicker = false
                    }
                ) {
                    Text(text = stringResource(R.string.action_confirm), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }
}

@Composable
private fun AddTaskTopBar(
    isLoading: Boolean = false,
    isSaveEnabled: Boolean = true,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
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
        TextButton(onClick = onCancelClick, enabled = !isLoading) {
            Text(
                text = stringResource(R.string.action_cancel),
                fontSize = 16.sp,
                color = PrimaryBlue
            )
        }

        Text(
            text = stringResource(R.string.add_task_topbar_title),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        TextButton(onClick = onSaveClick, enabled = !isLoading && isSaveEnabled) {
            Text(
                text = stringResource(R.string.action_save),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (!isLoading && isSaveEnabled) PrimaryBlue else PrimaryBlue.copy(alpha = 0.4f)
            )
        }
    }
}

@Preview(name = "Add Task Screen Light", showBackground = true)
@Preview(name = "Add Task Screen Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AddTaskScreenPreview() {
    UTHTodoTheme {
        AddTaskScreen()
    }
}
