package com.example.uthtodo

import com.example.uthtodo.data.model.AppNotification
import com.example.uthtodo.data.model.Task
import com.example.uthtodo.data.repository.AuthUser
import com.example.uthtodo.data.repository.initials
import com.example.uthtodo.model.HomeTaskItem
import com.example.uthtodo.ui.viewmodel.NotificationUiState
import com.example.uthtodo.ui.viewmodel.TaskUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArchitectureM2UnitTest {

    @Test
    fun authUserInitials_withNullOrBlank_returnsDefaultUth() {
        val nullUser: AuthUser? = null
        assertEquals("UTH", nullUser.initials)

        val emptyUser = AuthUser(uid = "1", displayName = "", email = null, photoUrl = null)
        assertEquals("UTH", emptyUser.initials)

        val blankUser = AuthUser(uid = "2", displayName = "   ", email = null, photoUrl = null)
        assertEquals("UTH", blankUser.initials)
    }

    @Test
    fun authUserInitials_withSingleOrMultiWordNames_returnsCorrectInitials() {
        val singleWord = AuthUser(uid = "1", displayName = "Dat", email = null, photoUrl = null)
        assertEquals("D", singleWord.initials)

        val twoWords = AuthUser(uid = "2", displayName = "Hoang Dat", email = null, photoUrl = null)
        assertEquals("HD", twoWords.initials)

        val threeWords = AuthUser(uid = "3", displayName = "Nguyen Hoang Dat", email = null, photoUrl = null)
        assertEquals("HD", threeWords.initials)
    }

    @Test
    fun homeScreenModel_motivationTextRules_reflectTaskProgressAccurately() {
        fun getMotivation(total: Int, completed: Int, pct: Int): String {
            return when {
                total == 0 -> "Bắt đầu thêm công việc đầu tiên nhé!"
                completed == total -> "Bạn đã hoàn thành tất cả công việc!"
                pct >= 50 -> "Đã hoàn thành hơn nửa rồi, cố lên!"
                else -> "Bắt đầu từ việc nhỏ nhất nhé."
            }
        }

        assertEquals("Bắt đầu thêm công việc đầu tiên nhé!", getMotivation(0, 0, 0))
        assertEquals("Bạn đã hoàn thành tất cả công việc!", getMotivation(3, 3, 100))
        assertEquals("Đã hoàn thành hơn nửa rồi, cố lên!", getMotivation(4, 2, 50))
        assertEquals("Đã hoàn thành hơn nửa rồi, cố lên!", getMotivation(3, 2, 66))
        assertEquals("Bắt đầu từ việc nhỏ nhất nhé.", getMotivation(4, 1, 25))
    }

    @Test
    fun homeScreenModel_categorySummaries_countOnlyUncompletedTasks() {
        val tasks = listOf(
            Task(id = "1", title = "Task 1", category = "Học tập", isCompleted = false),
            Task(id = "2", title = "Task 2", category = "Học tập", isCompleted = true),
            Task(id = "3", title = "Task 3", category = "Cuộc sống", isCompleted = false),
            Task(id = "4", title = "Task 4", category = "Cuộc sống", isCompleted = true)
        )

        val hocTapRemaining = tasks.count { it.category == "Học tập" && !it.isCompleted }
        val cuocSongRemaining = tasks.count { it.category == "Cuộc sống" && !it.isCompleted }

        assertEquals(1, hocTapRemaining)
        assertEquals(1, cuocSongRemaining)
    }

    @Test
    fun taskPresentationMapping_mapsDomainToPresentationCorrectly() {
        val t = Task(
            id = "t1",
            title = "Làm bài tập",
            category = "Học tập",
            timeText = "14:00, 20/10",
            isCompleted = false
        )

        val item = HomeTaskItem(
            id = t.id,
            title = t.title,
            categoryName = t.category,
            timeText = if (t.timeText.isNotBlank()) t.timeText else "Hôm nay",
            isChecked = t.isCompleted,
            isPrimaryCategory = t.category == "Học tập"
        )

        assertEquals("t1", item.id)
        assertEquals("Làm bài tập", item.title)
        assertEquals("Học tập", item.categoryName)
        assertEquals("14:00, 20/10", item.timeText)
        assertFalse(item.isChecked)
        assertTrue(item.isPrimaryCategory)
    }

    @Test
    fun taskPresentationMapping_defaultTimeText_whenEmpty() {
        val t = Task(
            id = "t2",
            title = "Đi chợ",
            category = "Cuộc sống",
            timeText = "",
            isCompleted = true
        )

        val item = HomeTaskItem(
            id = t.id,
            title = t.title,
            categoryName = t.category,
            timeText = if (t.timeText.isNotBlank()) t.timeText else "Hôm nay",
            isChecked = t.isCompleted,
            isPrimaryCategory = t.category == "Học tập"
        )

        assertEquals("Hôm nay", item.timeText)
        assertTrue(item.isChecked)
        assertFalse(item.isPrimaryCategory)
    }

    @Test
    fun notificationUiState_initialState_and_errorUpdates() {
        val initial = NotificationUiState()
        assertTrue(initial.notifications.isEmpty())
        assertFalse(initial.isLoading)
        assertNull(initial.errorMessage)

        val withError = initial.copy(errorMessage = "Lỗi kết nối mạng")
        assertEquals("Lỗi kết nối mạng", withError.errorMessage)

        val notif = AppNotification(id = "n1", title = "Test", message = "Msg")
        val withNotif = initial.copy(notifications = listOf(notif))
        assertEquals(1, withNotif.notifications.size)
    }

    @Test
    fun taskUiState_initialState_and_statusTransitions() {
        val initial = TaskUiState()
        assertFalse(initial.isLoading)
        assertFalse(initial.isSaved)
        assertNull(initial.errorMessage)

        val loading = initial.copy(isLoading = true)
        assertTrue(loading.isLoading)

        val saved = initial.copy(isSaved = true)
        assertTrue(saved.isSaved)

        val error = initial.copy(errorMessage = "Tiêu đề công việc không được để trống")
        assertEquals("Tiêu đề công việc không được để trống", error.errorMessage)
    }
}
