package com.openclassrooms.rebonnte

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.rebonnte.ui.aisle.Aisle
import com.openclassrooms.rebonnte.ui.aisle.AisleRepository
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.argThat
import kotlin.test.assertEquals

class AisleViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: AisleRepository
    private lateinit var aislesFlow: MutableStateFlow<List<Aisle>>
    private lateinit var viewModel: AisleViewModel

    @Before
    fun setup() {
        repository = mock(AisleRepository::class.java)
        aislesFlow = MutableStateFlow(emptyList())
        `when`(repository.aisles).thenReturn(aislesFlow)
        // Do not initialize viewModel here
    }


    /** ensures that the aisles property exposed by the AisleViewModel
    accurately reflects the data provided
    by the underlying repository.aisles Flow */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `aisles reflects repository aisles`() = runTest {
        val testAisle = Aisle(
            name = "Test Aisle",
            id = "testId",
            timestamp = System.currentTimeMillis()
        )
        aislesFlow.value = listOf(testAisle)
        viewModel = AisleViewModel(repository)
        val result = viewModel.aisles.first()
        assertEquals(1, result.size)
        assertEquals("Test Aisle", result[0].name)
        assertEquals("testId", result[0].id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `addRandomAisle adds new aisle with incremented name`() = runTest {
        val initialAisle = Aisle(
            name = "Aisle 1",
            id = "id1",
            timestamp = System.currentTimeMillis()
        )
        aislesFlow.value = listOf(initialAisle)
        viewModel = AisleViewModel(repository)
        viewModel.addRandomAisle()
        verify(repository).addAisle(
            argThat { aisle ->
                aisle.name == "Aisle 2" &&
                        aisle.id.isNotEmpty() &&
                        aisle.timestamp > 0
            }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `addRandomAisle adds first aisle when empty`() = runTest {
        aislesFlow.value = emptyList()
        viewModel = AisleViewModel(repository)
        viewModel.addRandomAisle()
        verify(repository).addAisle(
            argThat { aisle ->
                aisle.name == "Aisle 1" &&
                        aisle.id.isNotEmpty() &&
                        aisle.timestamp > 0
            }
        )
    }

    @Test
    fun `deleteAisle calls repository deleteAisle`() {
        val aisleToDelete = Aisle(
            name = "Aisle to Delete",
            id = "deleteId",
            timestamp = System.currentTimeMillis()
        )
        viewModel = AisleViewModel(repository)
        viewModel.deleteAisle(aisleToDelete)
        verify(repository).deleteAisle(aisleToDelete)
    }
}