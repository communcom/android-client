package io.golos.utils.dates_local_now_calculator

import io.golos.utils.format.fromServerFormat
import org.junit.Test
import org.junit.Assert.*

class ServerLocalNowDatesCalculatorTest {
    @Test
    fun test1() {
        // Arrange
        val calculator = ServerLocalNowDatesCalculator()
        val serverDate = "2020-02-10T15:22:15.000Z".fromServerFormat()

        // Act
        val base = calculator.calculateBase(serverDate)

        // Assert
        assertEquals(1, 1)          // Silly, for debug only
    }
}