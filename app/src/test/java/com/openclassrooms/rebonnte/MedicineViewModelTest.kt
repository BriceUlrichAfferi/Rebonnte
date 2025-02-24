package com.openclassrooms.rebonnte.ui.medicine

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.rebonnte.ui.aisle.Aisle
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.kotlin.argThat
import org.mockito.kotlin.check
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class MedicineViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MedicineViewModel
    private lateinit var repository: MedicineRepository
    private lateinit var medicinesFlow: MutableStateFlow<List<Medicine>>
    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var mockFirebaseUser: FirebaseUser

    @Before
    fun setup() {
        repository = mock(MedicineRepository::class.java)
        medicinesFlow = MutableStateFlow(emptyList())
        whenever(repository.medicines).thenReturn(medicinesFlow)

        mockFirebaseAuth = mock(FirebaseAuth::class.java)
        mockFirebaseUser = mock(FirebaseUser::class.java)
        whenever(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        whenever(mockFirebaseUser.email).thenReturn("test@example.com")

        mockStatic(FirebaseAuth::class.java).use { mockedAuth ->
            whenever(FirebaseAuth.getInstance()).thenReturn(mockFirebaseAuth)
            viewModel = MedicineViewModel(repository)
        }
    }

    @Test
    fun `medicines reflects repository medicines`() = runTest {
        val testMedicine = Medicine(name = "Aspirin", stock = 50, nameAisle = "Pain Relief", id = "med1", addedByEmail = "test@example.com")
        medicinesFlow.value = listOf(testMedicine)

        val result = viewModel.medicines.first()

        assertEquals(1, result.size)
        assertEquals("Aspirin", result[0].name)
    }


    @Test
    fun `addMedicine adds medicine with user email`() = runTest {
        mockStatic(FirebaseAuth::class.java).use { mockAuth ->
            val mockFirebaseAuth = mock(FirebaseAuth::class.java)
            val mockFirebaseUser = mock(FirebaseUser::class.java)

            // Mock FirebaseAuth.getInstance()
            whenever(FirebaseAuth.getInstance()).thenReturn(mockFirebaseAuth)
            whenever(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
            whenever(mockFirebaseUser.email).thenReturn("test@example.com")

            val medicine = Medicine(name = "Ibuprofen", stock = 30, nameAisle = "Pain Relief", id = "med2")
            viewModel.addMedicine(medicine)

            verify(repository).addMedicine(check { updatedMedicine ->
                assertEquals("Ibuprofen", updatedMedicine.name)
                assertEquals(30, updatedMedicine.stock)
                assertEquals("Pain Relief", updatedMedicine.nameAisle)
                assertEquals("med2", updatedMedicine.id)
                assertEquals("test@example.com", updatedMedicine.addedByEmail)
            })
        }
    }



    @Test
    fun `updateMedicine calls repository updateMedicine`() {
        val medicine = Medicine(name = "Paracetamol", stock = 20, nameAisle = "Fever", id = "med3")
        viewModel.updateMedicine(medicine)

        verify(repository).updateMedicine(medicine)
    }

    @Test
    fun `deleteMedicine calls repository deleteMedicine`() {
        val medicineId = "med4"
        viewModel.deleteMedicine(medicineId)

        verify(repository).deleteMedicine(medicineId)
    }
}
