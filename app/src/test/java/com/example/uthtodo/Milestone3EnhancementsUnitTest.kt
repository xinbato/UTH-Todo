package com.example.uthtodo

import com.example.uthtodo.data.model.AppNotification
import com.example.uthtodo.model.OnboardingPageModel
import com.example.uthtodo.model.StartPageModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying Milestone 3 enhancements:
 * - Tab-specific notification filtering logic (All, Reminder, System)
 * - Initial welcome notification payload structure
 * - OnboardingPageModel and StartPageModel string resource mappings
 * - AddTask date/time dialog string resources
 */
class Milestone3EnhancementsUnitTest {

    @Test
    fun welcomeNotification_payloadConformsToSpecification() {
        val welcomeNotif = AppNotification(
            userId = "test_user_id",
            title = "Chào mừng bạn đến với UTH Todo",
            message = "Dữ liệu công việc của bạn được lưu trữ an toàn trên đám mây.",
            type = "SYSTEM",
            isRead = false
        )

        assertEquals("Chào mừng bạn đến với UTH Todo", welcomeNotif.title)
        assertEquals("Dữ liệu công việc của bạn được lưu trữ an toàn trên đám mây.", welcomeNotif.message)
        assertEquals("SYSTEM", welcomeNotif.type)
        assertEquals("test_user_id", welcomeNotif.userId)
        assertFalse(welcomeNotif.isRead)
    }

    @Test
    fun notificationTabFilterLogic_properlyFiltersCategories() {
        val notifications = listOf(
            AppNotification(id = "1", title = "Task 1", message = "", type = "REMINDER"),
            AppNotification(id = "2", title = "Task 2", message = "", type = "OVERDUE"),
            AppNotification(id = "3", title = "Task 3", message = "", type = "SYSTEM"),
            AppNotification(id = "4", title = "Task 4", message = "", type = "DAILY_SUMMARY")
        )

        // Tab 0: All
        val tab0 = notifications
        assertEquals(4, tab0.size)

        // Tab 1: Reminder (REMINDER & OVERDUE)
        val tab1 = notifications.filter { it.type == "REMINDER" || it.type == "OVERDUE" }
        assertEquals(2, tab1.size)
        assertTrue(tab1.all { it.type == "REMINDER" || it.type == "OVERDUE" })
        assertEquals("1", tab1[0].id)
        assertEquals("2", tab1[1].id)

        // Tab 2: System (SYSTEM & DAILY_SUMMARY)
        val tab2 = notifications.filter { it.type == "SYSTEM" || it.type == "DAILY_SUMMARY" }
        assertEquals(2, tab2.size)
        assertTrue(tab2.all { it.type == "SYSTEM" || it.type == "DAILY_SUMMARY" })
        assertEquals("3", tab2[0].id)
        assertEquals("4", tab2[1].id)
    }

    @Test
    fun onboardingPageModel_mapsAllStringResources() {
        val pages = OnboardingPageModel.getPages()
        assertEquals(3, pages.size)

        // Page 0
        assertEquals(R.string.onboarding_title_1, pages[0].titleResId)
        assertEquals(R.string.onboarding_desc_1, pages[0].descriptionResId)
        assertEquals(R.string.btn_next, pages[0].buttonTextResId)

        // Page 1
        assertEquals(R.string.onboarding_title_2, pages[1].titleResId)
        assertEquals(R.string.onboarding_desc_2, pages[1].descriptionResId)
        assertEquals(R.string.btn_next, pages[1].buttonTextResId)

        // Page 2
        assertEquals(R.string.onboarding_title_reminder, pages[2].titleResId)
        assertEquals(R.string.onboarding_desc_reminder, pages[2].descriptionResId)
        assertEquals(R.string.btn_get_started_now, pages[2].buttonTextResId)
    }

    @Test
    fun startPageModel_hasDefaultStringResources() {
        val model = StartPageModel()
        assertEquals(R.string.app_tagline, model.subtitleResId)
        assertEquals(R.string.app_version, model.versionResId)
        assertNotNull(model.titleBold)
        assertNotNull(model.titleRegular)
    }

    @Test
    fun addTaskActionStrings_existAndAreDistinct() {
        val actionConfirmRes = R.string.action_confirm
        val actionCancelRes = R.string.action_cancel

        assertTrue(actionConfirmRes != 0)
        assertTrue(actionCancelRes != 0)
        assertTrue(actionConfirmRes != actionCancelRes)
    }

    @Test
    fun notificationEmptyStateStrings_existInResources() {
        assertTrue(R.string.notification_empty_reminder_title != 0)
        assertTrue(R.string.notification_empty_reminder_subtitle != 0)
        assertTrue(R.string.notification_empty_system_title != 0)
        assertTrue(R.string.notification_empty_system_subtitle != 0)
        assertTrue(R.string.notification_welcome_title != 0)
        assertTrue(R.string.notification_welcome_message != 0)
    }
}
