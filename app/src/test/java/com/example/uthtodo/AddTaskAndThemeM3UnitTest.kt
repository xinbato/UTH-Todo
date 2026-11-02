package com.example.uthtodo

import com.example.uthtodo.ui.screens.ProfileDataModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class AddTaskAndThemeM3UnitTest {

    @Test
    fun datePickerUtcMillis_extractionPreservesSelectedDate() {
        // Emulate Material 3 DatePicker selectedDateMillis: UTC midnight for 2026-11-15
        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(2026, Calendar.NOVEMBER, 15, 0, 0, 0)
        }
        val selectedMillis = utcCal.timeInMillis

        // When extracting in local calendar logic as in AddTaskScreen
        val extractedUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = selectedMillis
        }

        val localCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 30)
            set(Calendar.YEAR, extractedUtc.get(Calendar.YEAR))
            set(Calendar.MONTH, extractedUtc.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, extractedUtc.get(Calendar.DAY_OF_MONTH))
        }

        assertEquals(2026, localCal.get(Calendar.YEAR))
        assertEquals(Calendar.NOVEMBER, localCal.get(Calendar.MONTH))
        assertEquals(15, localCal.get(Calendar.DAY_OF_MONTH))
        // Verify time of day was preserved
        assertEquals(14, localCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(30, localCal.get(Calendar.MINUTE))
    }

    @Test
    fun timeFormatting_boundaryValuesFormatCorrectly() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
        }
        assertEquals("00:00", timeFormat.format(cal.time))

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        assertEquals("23:59", timeFormat.format(cal.time))

        cal.set(Calendar.HOUR_OF_DAY, 9)
        cal.set(Calendar.MINUTE, 5)
        assertEquals("09:05", timeFormat.format(cal.time))
    }

    @Test
    fun priorityResolution_getOrElseHandlesBoundaries() {
        val priorities = listOf("Thấp", "Vừa", "Cao")

        assertEquals("Thấp", priorities.getOrElse(0) { "Vừa" })
        assertEquals("Vừa", priorities.getOrElse(1) { "Vừa" })
        assertEquals("Cao", priorities.getOrElse(2) { "Vừa" })
        // Out of bound fallback
        assertEquals("Vừa", priorities.getOrElse(-1) { "Vừa" })
        assertEquals("Vừa", priorities.getOrElse(5) { "Vừa" })
    }

    @Test
    fun profileDataModel_defaultsAreValid() {
        val model = ProfileDataModel()
        assertEquals("UTH", model.userName)
        assertEquals("uth.guest@uth.edu.vn", model.userEmail)
        assertEquals(0, model.completedCount)
        assertEquals(0, model.streakDays)
        assertEquals(0, model.onTimeRate)
        assertEquals(false, model.isGoogleLinked)
        assertEquals(0, model.weeklyCompletedCount)
        assertEquals(0, model.weeklyTotalCount)
    }

    @Test
    fun viDateFormat_formatsDayAndMonthCorrectly() {
        val dateFormat = SimpleDateFormat("EEE, dd/MM", Locale.forLanguageTag("vi-VN"))
        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 2, 10, 0, 0)
        }
        val formatted = dateFormat.format(cal.time)
        assertNotNull(formatted)
        assert(formatted.contains("02/11"))
    }
}
