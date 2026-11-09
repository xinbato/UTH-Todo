package com.example.uthtodo.model

import com.example.uthtodo.R

sealed interface CardContentDetail {
    data class TaskListDetail(val tasks: List<TaskItemModel>) : CardContentDetail
    data class CategoryDetail(val categories: List<CategoryTagModel>) : CardContentDetail
    data class NotificationDetail(val reminder: ReminderItemModel) : CardContentDetail
}

data class TaskItemModel(
    val id: Int,
    val title: String = "",
    val isChecked: Boolean
)

data class CategoryTagModel(
    val id: Int,
    val name: String,
    val isPrimary: Boolean
)

data class ReminderItemModel(
    val id: Int,
    val text: String
)

enum class OnboardingCardType {
    TASK_LIST,     // Màn hình 1a
    CATEGORIES,    // Màn hình 1b
    NOTIFICATION   // Màn hình 1c
}

data class AppHeaderModel(
    val appNameBold: String = "UTH ",
    val appNameRegular: String = "Todo",
    val skipText: String = "Bỏ qua",
    val skipTextResId: Int? = R.string.btn_skip
)

data class OnboardingPageModel(
    val stepIndex: Int,
    val title: String,
    val description: String,
    val buttonText: String,
    val cardType: OnboardingCardType,
    val cardDetail: CardContentDetail,
    val nextRoute: String,
    val isLastPage: Boolean = false,
    val headerModel: AppHeaderModel = AppHeaderModel(),
    val titleResId: Int? = null,
    val descriptionResId: Int? = null,
    val buttonTextResId: Int? = null
) {
    val totalSteps: Int = 3

    companion object {
        fun getPageByStep(stepIndex: Int): OnboardingPageModel {
            return getPages().getOrElse(stepIndex) { getPages().first() }
        }

        fun getPages(): List<OnboardingPageModel> = listOf(
            OnboardingPageModel(
                stepIndex = 0,
                title = "Mọi việc, gọn trong một chỗ",
                description = "Ghi lại bài tập, việc nhà, lịch hẹn. Không còn quên việc nào.",
                buttonText = "Tiếp tục",
                cardType = OnboardingCardType.TASK_LIST,
                cardDetail = CardContentDetail.TaskListDetail(
                    tasks = listOf(
                        TaskItemModel(id = 1, isChecked = true),
                        TaskItemModel(id = 2, isChecked = false),
                        TaskItemModel(id = 3, isChecked = false)
                    )
                ),
                nextRoute = "onboarding_1b",
                titleResId = R.string.onboarding_title_1,
                descriptionResId = R.string.onboarding_desc_1,
                buttonTextResId = R.string.btn_next
            ),
            OnboardingPageModel(
                stepIndex = 1,
                title = "Chia theo Học tập và Cuộc sống",
                description = "Mỗi việc có nhóm riêng, nhìn là biết hôm nay cần lo gì.",
                buttonText = "Tiếp tục",
                cardType = OnboardingCardType.CATEGORIES,
                cardDetail = CardContentDetail.CategoryDetail(
                    categories = listOf(
                        CategoryTagModel(id = 1, name = "Học tập", isPrimary = true),
                        CategoryTagModel(id = 2, name = "Cuộc sống", isPrimary = false)
                    )
                ),
                nextRoute = "onboarding_1c",
                titleResId = R.string.onboarding_title_2,
                descriptionResId = R.string.onboarding_desc_2,
                buttonTextResId = R.string.btn_next
            ),
            OnboardingPageModel(
                stepIndex = 2,
                title = "Nhắc đúng lúc",
                description = "Bật thông báo để UTH Todo nhắc bạn trước hạn chót.",
                buttonText = "Bắt đầu ngay",
                cardType = OnboardingCardType.NOTIFICATION,
                cardDetail = CardContentDetail.NotificationDetail(
                    reminder = ReminderItemModel(id = 1, text = "Ôn bài Toán lúc 19:00")
                ),
                nextRoute = "start",
                isLastPage = true,
                titleResId = R.string.onboarding_title_reminder,
                descriptionResId = R.string.onboarding_desc_reminder,
                buttonTextResId = R.string.btn_get_started_now
            )
        )
    }
}

data class StartPageModel(
    val titleBold: String = "UTH",
    val titleRegular: String = " Todo",
    val subtitle: String = "Việc học, việc nhà — gọn gàng mỗi ngày",
    val version: String = "Phiên bản 1.0",
    val subtitleResId: Int? = R.string.app_tagline,
    val versionResId: Int? = R.string.app_version
)
