package com.openclassrooms.rebonnte

import com.openclassrooms.rebonnte.ui.history.History
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HistoryTest {

    /**
    Ensure that the data class properties are properly initialized
    and that the class behaves as expected
     */

    @Test
    fun `test History data class initialization`() {
        // Initialize a History object using the default constructor
        val history = History()

        // Ensure that the properties are initialized with default values
        assertEquals("", history.medicineName)
        assertEquals("", history.userId)
        assertEquals("", history.date)
        assertEquals("", history.details)

        // Now initialize a History object with specific values
        val specificHistory = History(medicineName = "Aspirin", userId = "user123", date = "2025-02-24", details = "Used for pain relief")

        // Ensure that the properties are set correctly
        assertEquals("Aspirin", specificHistory.medicineName)
        assertEquals("user123", specificHistory.userId)
        assertEquals("2025-02-24", specificHistory.date)
        assertEquals("Used for pain relief", specificHistory.details)
    }

    @Test
    fun `test History data class non-null properties`() {
        // Initialize a History object with non-default values
        val history = History(medicineName = "Ibuprofen", userId = "user456", date = "2025-02-25", details = "Used for inflammation")

        // Ensure that all properties are not null
        assertNotNull(history.medicineName)
        assertNotNull(history.userId)
        assertNotNull(history.date)
        assertNotNull(history.details)
    }
}
