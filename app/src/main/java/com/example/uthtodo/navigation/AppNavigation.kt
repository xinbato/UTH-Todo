package com.example.uthtodo.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uthtodo.R
import com.example.uthtodo.data.repository.initials
import com.example.uthtodo.model.OnboardingPageModel
import com.example.uthtodo.ui.screens.AddTaskScreen
import com.example.uthtodo.ui.screens.HomeScreen
import com.example.uthtodo.ui.screens.LoginScreen
import com.example.uthtodo.ui.screens.OnboardingScreen
import com.example.uthtodo.ui.screens.NotificationScreen
import com.example.uthtodo.ui.screens.ProfileDataModel
import com.example.uthtodo.ui.screens.ProfileScreen
import com.example.uthtodo.ui.screens.SettingsScreen
import com.example.uthtodo.ui.screens.StartScreen
import com.example.uthtodo.ui.screens.TaskDetailDataModel
import com.example.uthtodo.ui.screens.TaskDetailScreen
import com.example.uthtodo.ui.screens.TaskItemModel
import com.example.uthtodo.ui.screens.TaskListScreen
import com.example.uthtodo.ui.screens.TaskListScreenModel
import com.example.uthtodo.ui.viewmodel.LoginViewModel
import com.example.uthtodo.ui.viewmodel.NotificationViewModel
import com.example.uthtodo.ui.viewmodel.SettingsViewModel
import com.example.uthtodo.ui.viewmodel.TaskViewModel
import java.util.Calendar

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    notificationViewModel: NotificationViewModel = viewModel()
) {
    val context = LocalContext.current
    val loginUiState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val taskUiState by taskViewModel.uiState.collectAsStateWithLifecycle()
    val tasks by taskViewModel.tasks.collectAsStateWithLifecycle()
    val selectedTask by taskViewModel.selectedTask.collectAsStateWithLifecycle()
    val notifications by notificationViewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotificationCount by notificationViewModel.unreadCount.collectAsStateWithLifecycle()
    val notificationUiState by notificationViewModel.uiState.collectAsStateWithLifecycle()

    // Handle Notification error message
    LaunchedEffect(notificationUiState.errorMessage) {
        notificationUiState.errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            notificationViewModel.clearError()
        }
    }

    // Handle Task save & error
    LaunchedEffect(taskUiState.isSaved) {
        if (taskUiState.isSaved) {
            Toast.makeText(context, context.getString(R.string.toast_task_saved), Toast.LENGTH_SHORT).show()
            taskViewModel.resetState()
            navController.popBackStack()
        }
    }

    LaunchedEffect(taskUiState.errorMessage) {
        taskUiState.errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            taskViewModel.clearError()
        }
    }

    // Handle Auth error message
    LaunchedEffect(loginUiState.errorMessage) {
        loginUiState.errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            loginViewModel.clearError()
        }
    }

    // Navigate to Home upon successful authentication
    LaunchedEffect(loginUiState.isSuccess) {
        if (loginUiState.isSuccess && navController.currentDestination?.route == ScreenRoute.Login.route) {
            navController.navigate(ScreenRoute.Home.route) {
                popUpTo(ScreenRoute.Start.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = ScreenRoute.Start.route,
        modifier = modifier
    ) {
        composable(ScreenRoute.Start.route) {
            StartScreen(
                onStartClick = {
                    if (loginUiState.isSuccess) {
                        navController.navigate(ScreenRoute.Home.route) {
                            popUpTo(ScreenRoute.Start.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(ScreenRoute.Onboarding1a.route)
                    }
                }
            )
        }

        composable(ScreenRoute.Onboarding1a.route) {
            val pageModel = OnboardingPageModel.getPages()[0]
            OnboardingScreen(
                pageModel = pageModel,
                onNextClick = {
                    navController.navigate(ScreenRoute.Onboarding1b.route)
                },
                onSkipClick = {
                    navController.navigate(ScreenRoute.Login.route)
                }
            )
        }

        composable(ScreenRoute.Onboarding1b.route) {
            val pageModel = OnboardingPageModel.getPages()[1]
            OnboardingScreen(
                pageModel = pageModel,
                onNextClick = {
                    navController.navigate(ScreenRoute.Onboarding1c.route)
                },
                onSkipClick = {
                    navController.navigate(ScreenRoute.Login.route)
                }
            )
        }

        composable(ScreenRoute.Onboarding1c.route) {
            val pageModel = OnboardingPageModel.getPages()[2]
            OnboardingScreen(
                pageModel = pageModel,
                onNextClick = {
                    navController.navigate(ScreenRoute.Login.route) {
                        popUpTo(ScreenRoute.Start.route) { inclusive = true }
                    }
                },
                onSkipClick = {
                    navController.navigate(ScreenRoute.Login.route) {
                        popUpTo(ScreenRoute.Start.route) { inclusive = true }
                    }
                }
            )
        }

        composable(ScreenRoute.Login.route) {
            LoginScreen(
                isLoading = loginUiState.isLoading,
                onBackClick = {
                    navController.popBackStack()
                },
                onGoogleLoginClick = {
                    loginViewModel.signInWithGoogle(context)
                },
                onGuestClick = {
                    loginViewModel.signInAnonymously()
                }
            )
        }

        composable(ScreenRoute.Home.route) {
            val homeUiState by taskViewModel.homeUiState.collectAsStateWithLifecycle()

            HomeScreen(
                model = homeUiState,
                unreadNotificationCount = unreadNotificationCount,
                onNotificationClick = {
                    navController.navigate(ScreenRoute.Notification.route)
                },
                onTaskToggle = { taskId ->
                    val task = tasks.find { it.id == taskId }
                    if (task != null) {
                        taskViewModel.toggleTask(task.id, !task.isCompleted)
                    }
                },
                onTaskClick = { taskId ->
                    val task = tasks.find { it.id == taskId }
                    taskViewModel.selectTask(task)
                    navController.navigate(ScreenRoute.TaskDetail.route)
                },
                onAddTaskClick = {
                    navController.navigate(ScreenRoute.AddTask.route)
                },
                onTabSelected = { index ->
                    when (index) {
                        1 -> navController.navigate(ScreenRoute.TaskList.route)
                        2 -> navController.navigate(ScreenRoute.Notification.route)
                        3 -> navController.navigate(ScreenRoute.Settings.route)
                    }
                },
                onNavigateToTaskList = {
                    navController.navigate(ScreenRoute.TaskList.route)
                }
            )
        }

        composable(ScreenRoute.TaskList.route) {
            val pendingList = tasks.filter { !it.isCompleted }.map { t ->
                TaskItemModel(
                    id = t.id,
                    title = t.title,
                    categoryName = t.category,
                    timeText = if (t.timeText.isNotBlank()) t.timeText else "Hôm nay",
                    isChecked = false,
                    isPrimaryCategory = t.category == "Học tập",
                    isUrgent = t.priority == "Cao"
                )
            }
            val completedList = tasks.filter { it.isCompleted }.map { t ->
                TaskItemModel(
                    id = t.id,
                    title = t.title,
                    categoryName = t.category,
                    timeText = if (t.timeText.isNotBlank()) t.timeText else "Đã xong",
                    isChecked = true,
                    isPrimaryCategory = t.category == "Học tập",
                    isUrgent = false
                )
            }
            val listModel = TaskListScreenModel(
                uncompletedCount = pendingList.size,
                pendingTasks = pendingList,
                completedTasks = completedList
            )

            TaskListScreen(
                model = listModel,
                onTaskToggle = { taskId ->
                    val task = tasks.find { it.id == taskId }
                    if (task != null) {
                        taskViewModel.toggleTask(task.id, !task.isCompleted)
                    }
                },
                onTaskClick = { taskId ->
                    val task = tasks.find { it.id == taskId }
                    taskViewModel.selectTask(task)
                    navController.navigate(ScreenRoute.TaskDetail.route)
                },
                onTaskDelete = { taskId ->
                    taskViewModel.deleteTask(taskId)
                },
                onTabSelected = { index ->
                    when (index) {
                        0 -> navController.navigate(ScreenRoute.Home.route) { popUpTo(ScreenRoute.Home.route) { inclusive = true } }
                        2 -> navController.navigate(ScreenRoute.Notification.route)
                        3 -> navController.navigate(ScreenRoute.Settings.route)
                    }
                },
                onAddTaskClick = {
                    navController.navigate(ScreenRoute.AddTask.route)
                }
            )
        }

        composable(ScreenRoute.AddTask.route) {
            AddTaskScreen(
                isLoading = taskUiState.isLoading,
                onCancelClick = {
                    taskViewModel.resetState()
                    navController.popBackStack()
                },
                onSaveClick = { title, note, category, priority, dueDate, hasReminder ->
                    taskViewModel.addTask(
                        title = title,
                        note = note,
                        category = category,
                        priority = priority,
                        dueDate = dueDate,
                        hasReminder = hasReminder
                    )
                }
            )
        }

        composable(ScreenRoute.Notification.route) {
            NotificationScreen(
                notifications = notifications,
                unreadCount = unreadNotificationCount,
                onNotificationClick = { item ->
                    notificationViewModel.markAsRead(item.id)
                    if (!item.taskId.isNullOrBlank()) {
                        val task = tasks.find { it.id == item.taskId }
                        if (task != null) {
                            taskViewModel.selectTask(task)
                            navController.navigate(ScreenRoute.TaskDetail.route)
                        }
                    }
                },
                onMarkAllReadClick = {
                    notificationViewModel.markAllAsRead()
                },
                onDeleteNotification = { notificationId ->
                    notificationViewModel.deleteNotification(notificationId)
                },
                onTabSelected = { index ->
                    when (index) {
                        0 -> navController.navigate(ScreenRoute.Home.route) { popUpTo(ScreenRoute.Home.route) { inclusive = true } }
                        1 -> navController.navigate(ScreenRoute.TaskList.route)
                        3 -> navController.navigate(ScreenRoute.Settings.route)
                    }
                },
                onAddTaskClick = {
                    navController.navigate(ScreenRoute.AddTask.route)
                }
            )
        }

        composable(ScreenRoute.Settings.route) {
            val user = loginUiState.user
            val isGuest = user == null || user.isAnonymous
            val displayName = if (!isGuest && !user?.displayName.isNullOrBlank()) user!!.displayName!! else "UTH"
            val avatarUrl = if (!isGuest) user?.photoUrl else null
            val initials = user.initials
            val themeMode by settingsViewModel.themeMode.collectAsStateWithLifecycle()
            val primaryColorIndex by settingsViewModel.primaryColorIndex.collectAsStateWithLifecycle()
            val fontScale by settingsViewModel.fontScale.collectAsStateWithLifecycle()
            val isReminderEnabled by settingsViewModel.isReminderEnabled.collectAsStateWithLifecycle()
            val isSoundEnabled by settingsViewModel.isSoundEnabled.collectAsStateWithLifecycle()
            val isSummaryEnabled by settingsViewModel.isSummaryEnabled.collectAsStateWithLifecycle()
            val languageCode by settingsViewModel.languageCode.collectAsStateWithLifecycle()

            SettingsScreen(
                themeMode = themeMode,
                onThemeModeChange = { settingsViewModel.setThemeMode(it) },
                primaryColorIndex = primaryColorIndex,
                onPrimaryColorChange = { settingsViewModel.setPrimaryColorIndex(it) },
                fontScale = fontScale,
                onFontScaleChange = { settingsViewModel.setFontScale(it) },
                languageCode = languageCode,
                onLanguageChange = { settingsViewModel.setLanguageCode(it) },
                isReminderEnabled = isReminderEnabled,
                onReminderToggle = { settingsViewModel.setReminderEnabled(it) },
                isSoundEnabled = isSoundEnabled,
                onSoundToggle = { settingsViewModel.setSoundEnabled(it) },
                isSummaryEnabled = isSummaryEnabled,
                onSummaryToggle = { settingsViewModel.setSummaryEnabled(it) },
                avatarInitials = initials,
                avatarUrl = avatarUrl,
                onProfileClick = {
                    navController.navigate(ScreenRoute.Profile.route)
                },
                onTabSelected = { index ->
                    when (index) {
                        0 -> navController.navigate(ScreenRoute.Home.route) { popUpTo(ScreenRoute.Home.route) { inclusive = true } }
                        1 -> navController.navigate(ScreenRoute.TaskList.route)
                        2 -> navController.navigate(ScreenRoute.Notification.route)
                    }
                },
                onAddTaskClick = {
                    navController.navigate(ScreenRoute.AddTask.route)
                }
            )
        }

        composable(ScreenRoute.Profile.route) {
            val user = loginUiState.user
            val isGuest = user == null || user.isAnonymous
            val displayName = if (!isGuest && !user?.displayName.isNullOrBlank()) user!!.displayName!! else "UTH"
            val email = if (!isGuest && !user?.email.isNullOrBlank()) user!!.email!! else "Dùng thử không cần tài khoản"
            val avatarUrl = if (!isGuest) user?.photoUrl else null
            val initials = user.initials

            val completedCount = tasks.count { it.isCompleted }
            val totalCount = tasks.size
            val onTimeRate = if (totalCount > 0) (completedCount * 100 / totalCount) else 0
            val streakDays = if (completedCount > 0) 1 else 0

            val cal = Calendar.getInstance().apply {
                firstDayOfWeek = Calendar.MONDAY
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfWeek = cal.timeInMillis
            val endOfWeek = startOfWeek + (7 * 24 * 60 * 60 * 1000L)

            val weeklyTasks = tasks.filter { task ->
                val taskTime = task.dueDate?.time ?: task.createdAt?.time
                taskTime != null && taskTime in startOfWeek until endOfWeek
            }

            val (weeklyTotal, weeklyCompleted) = if (weeklyTasks.isNotEmpty() || tasks.isEmpty()) {
                weeklyTasks.size to weeklyTasks.count { it.isCompleted }
            } else {
                totalCount to completedCount
            }

            val dailyCompletions = MutableList(7) { 0 }
            if (weeklyTasks.isNotEmpty()) {
                weeklyTasks.filter { it.isCompleted }.forEach { task ->
                    val d = task.dueDate ?: task.createdAt
                    if (d != null) {
                        val c = Calendar.getInstance().apply { time = d }
                        val day = c.get(Calendar.DAY_OF_WEEK)
                        val dayIndex = if (day == Calendar.SUNDAY) 6 else day - Calendar.MONDAY
                        if (dayIndex in 0..6) {
                            dailyCompletions[dayIndex]++
                        }
                    }
                }
            } else if (weeklyCompleted > 0) {
                val todayCal = Calendar.getInstance()
                val todayDow = todayCal.get(Calendar.DAY_OF_WEEK)
                val todayIndex = if (todayDow == Calendar.SUNDAY) 6 else todayDow - Calendar.MONDAY
                dailyCompletions[todayIndex] = weeklyCompleted
            }

            ProfileScreen(
                model = ProfileDataModel(
                    userName = displayName,
                    userEmail = email,
                    avatarInitials = initials,
                    avatarUrl = avatarUrl,
                    completedCount = completedCount,
                    streakDays = streakDays,
                    onTimeRate = onTimeRate,
                    isGoogleLinked = !isGuest,
                    weeklyCompletedCount = weeklyCompleted,
                    weeklyTotalCount = weeklyTotal,
                    weeklyDailyCompletions = dailyCompletions
                ),
                onBackClick = {
                    navController.popBackStack()
                },
                onLogoutClick = {
                    loginViewModel.signOut()
                    navController.navigate(ScreenRoute.Login.route) {
                        popUpTo(ScreenRoute.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(ScreenRoute.TaskDetail.route) {
            val currentTask = tasks.find { it.id == selectedTask?.id } ?: selectedTask
            val detailModel = if (currentTask != null) {
                TaskDetailDataModel(
                    id = currentTask.id,
                    title = currentTask.title,
                    categoryName = currentTask.category,
                    priorityText = currentTask.priority,
                    dueDateText = if (currentTask.timeText.isNotBlank()) currentTask.timeText else "Hôm nay",
                    reminderText = if (currentTask.hasReminder) "Trước 15 phút" else "Không có",
                    repeatText = "Không",
                    noteText = currentTask.note,
                    isDone = currentTask.isCompleted,
                    subtasks = currentTask.subtasks
                )
            } else {
                TaskDetailDataModel()
            }

            TaskDetailScreen(
                model = detailModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = {
                    navController.navigate(ScreenRoute.AddTask.route)
                },
                onCompleteToggle = {
                    if (currentTask != null) {
                        taskViewModel.toggleTask(currentTask.id, !currentTask.isCompleted)
                        navController.popBackStack()
                    }
                },
                onDeleteClick = {
                    if (currentTask != null) {
                        taskViewModel.deleteTask(currentTask.id)
                        navController.popBackStack()
                    }
                },
                onAddSubtask = { subtaskTitle ->
                    if (currentTask != null) {
                        taskViewModel.addSubtask(currentTask.id, subtaskTitle)
                    }
                },
                onSubtaskToggle = { subtaskId, isCompleted ->
                    if (currentTask != null) {
                        taskViewModel.toggleSubtask(currentTask.id, subtaskId, isCompleted)
                    }
                },
                onSubtaskDelete = { subtaskId ->
                    if (currentTask != null) {
                        taskViewModel.deleteSubtask(currentTask.id, subtaskId)
                    }
                }
            )
        }
    }
}
