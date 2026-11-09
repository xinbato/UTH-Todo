package com.example.uthtodo.navigation

sealed class ScreenRoute(val route: String) {
    object Start : ScreenRoute("start")
    object Onboarding1a : ScreenRoute("onboarding_1a")
    object Onboarding1b : ScreenRoute("onboarding_1b")
    object Onboarding1c : ScreenRoute("onboarding_1c")
    object Login : ScreenRoute("login")
    object Home : ScreenRoute("home")
    object TaskList : ScreenRoute("task_list")
    object AddTask : ScreenRoute("add_task")
    object Notification : ScreenRoute("notification")
    object Settings : ScreenRoute("settings")
    object Profile : ScreenRoute("profile")
    object TaskDetail : ScreenRoute("task_detail")
}
