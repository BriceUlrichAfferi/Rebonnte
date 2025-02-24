package com.openclassrooms.rebonnte

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.rebonnte.ui.aisle.Aisle
import com.openclassrooms.rebonnte.ui.aisle.AisleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AisleRepositoryTest {

    private lateinit var repository: AisleRepository
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var collection: CollectionReference
    private lateinit var mockQuery: Query

    @Before
    fun setup() {
        mockFirestore = mock(FirebaseFirestore::class.java)
        collection = mock(CollectionReference::class.java)
        mockQuery = mock(Query::class.java)

        `when`(mockFirestore.collection("aisles")).thenReturn(collection)
        `when`(collection.orderBy("timestamp", Query.Direction.ASCENDING)).thenReturn(mockQuery)

        repository = AisleRepository(mockFirestore)
    }

    @After
    fun tearDown() {
        Mockito.framework().clearInlineMocks()
    }

    @Test
    fun `loadAisles fetches and updates aisles flow`() = runBlocking {
        // Arrange
        val mockSnapshot = mock(QuerySnapshot::class.java)
        val mockDocument = mock(DocumentSnapshot::class.java)
        val mockListenerRegistration = mock(ListenerRegistration::class.java)

        val testAisle = com.openclassrooms.rebonnte.ui.aisle.Aisle(
            name = "Test Aisle",
            id = "testId",
            timestamp = System.currentTimeMillis()
        )

        `when`(mockDocument.toObject(com.openclassrooms.rebonnte.ui.aisle.Aisle::class.java)).thenReturn(testAisle)
        `when`(mockSnapshot.documents).thenReturn(listOf(mockDocument))

        // Capture the listener before initializing the repository
        var listener: EventListener<QuerySnapshot>? = null
        `when`(mockQuery.addSnapshotListener(any<EventListener<QuerySnapshot>>())).thenAnswer { invocation ->
            listener = invocation.arguments[0] as EventListener<QuerySnapshot>
            mockListenerRegistration
        }

        // Initialize repository after setting up mocks
        repository = AisleRepository(mockFirestore)

        // Simulate the snapshot event
        listener?.onEvent(mockSnapshot, null)

        // Act - Collect from the flow
        val result = repository.aisles.first()

        // Assert
        assertEquals(1, result.size)
        assertEquals("Test Aisle", result[0].name)
        assertEquals("testId", result[0].id)
    }

    @Test
    fun `loadAisles adds default aisle when empty`() = runTest {
        // Arrange
        val mockSnapshot = mock(QuerySnapshot::class.java)
        val mockListenerRegistration = mock(ListenerRegistration::class.java)
        val mockDocumentReference = mock(DocumentReference::class.java)

        `when`(mockSnapshot.documents).thenReturn(emptyList())
        `when`(collection.document(any())).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.set(any())).thenReturn(mock())

        var listener: EventListener<QuerySnapshot>? = null
        `when`(mockQuery.addSnapshotListener(any<EventListener<QuerySnapshot>>())).thenAnswer { invocation ->
            listener = invocation.arguments[0] as EventListener<QuerySnapshot>
            mockListenerRegistration
        }

        repository = AisleRepository(mockFirestore)
        listener?.onEvent(mockSnapshot, null)

        // Act and Assert
        Mockito.verify(collection).document(any())
        Mockito.verify(mockDocumentReference).set(any<com.openclassrooms.rebonnte.ui.aisle.Aisle>())
    }

    @Test
    fun `addAisle adds new aisle to Firestore`() {
        // Arrange
        val testAisle = com.openclassrooms.rebonnte.ui.aisle.Aisle(
            name = "New Aisle",
            id = "newId",
            timestamp = System.currentTimeMillis()
        )
        val mockDocumentReference = mock(DocumentReference::class.java)
        `when`(collection.document(testAisle.id)).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.set(testAisle)).thenReturn(mock())

        // Act
        repository.addAisle(testAisle)

        // Assert
        Mockito.verify(collection).document(testAisle.id)
        Mockito.verify(mockDocumentReference).set(testAisle)
    }

    @Test
    fun `deleteAisle removes aisle from Firestore`() {
        // Arrange
        val testAisle = com.openclassrooms.rebonnte.ui.aisle.Aisle(
            name = "Delete Aisle",
            id = "deleteId",
            timestamp = System.currentTimeMillis()
        )
        val mockDocumentReference = mock(DocumentReference::class.java)
        `when`(collection.document(testAisle.id)).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.delete()).thenReturn(mock())

        // Act
        repository.deleteAisle(testAisle)

        // Assert
        Mockito.verify(collection).document(testAisle.id)
        Mockito.verify(mockDocumentReference).delete()
    }
}