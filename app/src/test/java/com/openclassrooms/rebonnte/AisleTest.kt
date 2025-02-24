package com.openclassrooms.rebonnte

import com.openclassrooms.rebonnte.ui.aisle.Aisle
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AisleTest {

    /**
    Ensure that the data class properties are properly initialized
    and that the class behaves as expected
     */

    @Test
    fun `test Aisle data class initialization`() {
        // Initialize an Aisle object using the default constructor
        val aisle = Aisle()

        // Ensure that the properties are initialized with default values
        assertEquals("", aisle.name)
        assertNotNull(aisle.id)  // Ensure the ID is not null, since it's generated using UUID
        assertTrue(aisle.id.isNotEmpty())  // Ensure that the generated UUID is not empty
        assertTrue(aisle.timestamp <= System.currentTimeMillis())  // Ensure timestamp is a valid time
    }

    @Test
    fun `test Aisle data class specific initialization`() {
        // Initialize an Aisle object with specific values
        val specificAisle = Aisle(name = "Pain Relief", id = "1234-5678-91011", timestamp = 1633036800000L)

        // Ensure that the properties are set correctly
        assertEquals("Pain Relief", specificAisle.name)
        assertEquals("1234-5678-91011", specificAisle.id)
        assertEquals(1633036800000L, specificAisle.timestamp)
    }

    @Test
    fun `test Aisle data class non-null properties`() {
        // Initialize an Aisle object with non-default values
        val aisle = Aisle(name = "Allergy", id = UUID.randomUUID().toString(), timestamp = 1623036800000L)

        // Ensure that all properties are not null
        assertNotNull(aisle.name)
        assertNotNull(aisle.id)
        assertNotNull(aisle.timestamp)
    }
}
