package com.openclassrooms.rebonnte

import com.openclassrooms.rebonnte.ui.history.History
import com.openclassrooms.rebonnte.ui.medicine.Medicine
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MedicineTest {

    /**
    Ensure that the data class properties are properly initialized
    and that the class behaves as expected
     */

    @Test
    fun `test Medicine data class initialization with default values`() {
        // Initialize a Medicine object using the default constructor
        val medicine = Medicine()

        // Ensure that the properties are initialized with default values
        assertEquals("", medicine.name)
        assertEquals(0, medicine.stock)
        assertEquals("", medicine.nameAisle)
        assertEquals(emptyList<History>(), medicine.histories)  // Should be empty list by default
        assertNotNull(medicine.id)  // Ensure the ID is not null, since it's generated using UUID
        assertTrue(medicine.id.isNotEmpty())  // Ensure that the generated UUID is not empty
        assertEquals("", medicine.addedByEmail)  // Ensure the addedByEmail is an empty string by default
    }

    @Test
    fun `test Medicine data class initialization with specific values`() {
        // Initialize a Medicine object with specific values
        val history1 = History(medicineName = "Aspirin", userId = "user123", date = "2022-01-01", details = "No side effects")
        val history2 = History(medicineName = "Tylenol", userId = "user456", date = "2022-02-01", details = "Minor side effects")

        val specificMedicine = Medicine(
            name = "Ibuprofen",
            stock = 50,
            nameAisle = "Pain Relief",
            histories = listOf(history1, history2),
            id = "1234-5678-91011",
            addedByEmail = "test@example.com"
        )

        // Ensure that the properties are set correctly
        assertEquals("Ibuprofen", specificMedicine.name)
        assertEquals(50, specificMedicine.stock)
        assertEquals("Pain Relief", specificMedicine.nameAisle)
        assertEquals(2, specificMedicine.histories.size)
        assertEquals("1234-5678-91011", specificMedicine.id)
        assertEquals("test@example.com", specificMedicine.addedByEmail)
    }

    @Test
    fun `test Medicine data class non-null properties`() {
        // Initialize a Medicine object with non-default values
        val history = History(medicineName = "Paracetamol", userId = "user789", date = "2022-03-01", details = "No side effects")
        val medicine = Medicine(
            name = "Amoxicillin",
            stock = 100,
            nameAisle = "Antibiotics",
            histories = listOf(history),
            id = UUID.randomUUID().toString(),
            addedByEmail = "admin@example.com"
        )

        // Ensure that all properties are not null
        assertNotNull(medicine.name)
        assertNotNull(medicine.stock)
        assertNotNull(medicine.nameAisle)
        assertNotNull(medicine.histories)
        assertNotNull(medicine.id)
        assertNotNull(medicine.addedByEmail)
    }
}
