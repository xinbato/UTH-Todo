package com.example.uthtodo.model

data class UserHeaderModel(
    val userName: String = "Chào UTH!",
    val dateText: String = "Thứ Ba, 20 tháng 10",
    val avatarInitials: String = "UTH",
    val avatarUrl: String? = null
)

data class TodayProgressModel(
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val percentage: Int = 0,
    val motivationText: String = "Bắt đầu từ việc nhỏ nhất nhé."
)

data class CategorySummaryModel(
    val id: Int,
    val name: String,
    val remainingCount: Int,
    val isPrimary: Boolean
)

data class HomeTaskItem(
    val id: String,
    val title: String,
    val categoryName: String,
    val timeText: String,
    val isChecked: Boolean,
    val isPrimaryCategory: Boolean
)

data class HomeScreenModel(
    val userHeader: UserHeaderModel = UserHeaderModel(),
    val progress: TodayProgressModel = TodayProgressModel(),
    val categories: List<CategorySummaryModel> = listOf(
        CategorySummaryModel(1, "Học tập", 0, true),
        CategorySummaryModel(2, "Cuộc sống", 0, false)
    ),
    val todayTasks: List<HomeTaskItem> = emptyList()
)
