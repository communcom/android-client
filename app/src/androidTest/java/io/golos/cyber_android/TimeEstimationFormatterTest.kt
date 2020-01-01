package io.golos.cyber_android

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.golos.cyber_android.application.shared.resources.AppResourcesProviderImpl
import io.golos.cyber_android.ui.common.formatters.time_estimation.TimeEstimationFormatter
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import org.junit.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class TimeEstimationFormatterTest {
    companion object {
        // Time units in milliseconds
        const val second = 1000L
        const val minute = second * 60
        const val hour = minute * 60
        const val day = hour * 24
        const val week = day * 7
        const val month = day * 30     // :) - was allowed by Vlad

        private lateinit var formatter: TimeEstimationFormatter

        @BeforeClass
        @JvmStatic
        fun setUp() {
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
            formatter = TimeEstimationFormatter(AppResourcesProviderImpl(appContext))
        }
    }

    @Test
    fun inFuture() {
        // Arrange
        val source = Date(Date().time+1000L)

        // Act
        val testResult = formatter.format(source)

        // Assert
        assertEquals("just now", testResult)
    }

    @Test
    fun now() {
        // Arrange
        val source = Date()

        // Act
        val testResult = formatter.format(source)

        // Assert
        assertEquals("just now", testResult)
    }

    @Test
    fun lessThanMinute() {
        // Arrange
        val source = Date(Date().time - 58 * second)

        // Act
        val testResult = formatter.format(source)

        // Assert
        assertEquals("just now", testResult)
    }

    @Test
    fun lessThanHour() {
        // Arrange
        val source = Date(Date().time - 58 * minute)

        // Act
        val testResult = formatter.format(source)

        // Assert
        assertEquals("58 minutes ago", testResult)
    }

    @Test
    fun lessThanDay() {
        // Arrange
        val source = Date(Date().time - 23 * hour)

        // Act
        val testResult = formatter.format(source)

        // Assert
        assertEquals("23 hours ago", testResult)
    }

    @Test
    fun lessThanWeek() {
        // Arrange
        val source = Date(Date().time - 6 * day)

        // Act
        val testResult = formatter.format(source)

        // Assert
        assertEquals("6 days ago", testResult)
    }

    @Test
    fun lessThanMonth() {
        // Arrange
        val source = Date(Date().time - 3 * week)

        // Act
        val testResult = formatter.format(source)

        // Assert
        assertEquals("3 weeks ago", testResult)
    }

    @Test
    fun moreThanMonth() {
        // Arrange
        val source = Date(Date().time - 31 * day)

        // Act
        val testResult = formatter.format(source)

        // Assert
        assertEquals("09.13.2019", testResult)
    }
}