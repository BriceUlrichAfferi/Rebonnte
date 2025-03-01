package com.openclassrooms.rebonnte

import com.google.android.gms.tasks.Task
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
import org.mockito.ArgumentMatchers.argThat
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
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
        val mockFirestore = mock<FirebaseFirestore>()
        val mockCollection = mock<CollectionReference>()
        val mockQuery = mock<Query>()
        val mockSnapshot = mock<QuerySnapshot>()
        val mockListenerRegistration = mock<ListenerRegistration>()
        val mockDocumentReference = mock<DocumentReference>()
        val mockTask = mock<Task<Void>>()

        // Mock Firestore setup
        `when`(mockFirestore.collection("aisles")).thenReturn(mockCollection)
        `when`(mockCollection.orderBy("timestamp", Query.Direction.ASCENDING)).thenReturn(mockQuery)
        `when`(mockSnapshot.documents).thenReturn(emptyList())
        `when`(mockCollection.document(any())).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.set(any())).thenReturn(mockTask)
        `when`(mockTask.addOnSuccessListener(any())).thenReturn(mockTask) // Chainable Task
        `when`(mockTask.addOnFailureListener(any())).thenReturn(mockTask) // Chainable Task

        var listener: EventListener<QuerySnapshot>? = null
        `when`(mockQuery.addSnapshotListener(any<EventListener<QuerySnapshot>>())).thenAnswer { invocation ->
            listener = invocation.arguments[0] as EventListener<QuerySnapshot>
            mockListenerRegistration
        }

        // Initialize repository
        val repository = AisleRepository(mockFirestore)

        // Act: Simulate empty snapshot
        listener?.onEvent(mockSnapshot, null)

        // Assert: Verify that a default aisle is added
        verify(mockCollection).document(any())
        verify(mockDocumentReference).set(argThat { it is Aisle && it.name == "Aisle 1" })
    }

    @Test
    fun `addAisle adds new aisle to Firestore`() {
        // Arrange
        val mockFirestore = mock<FirebaseFirestore>()
        val mockCollection = mock<CollectionReference>()
        val mockQuery = mock<Query>()
        val mockDocumentReference = mock<DocumentReference>()
        val mockTask = mock<Task<Void>>()
        val mockListenerRegistration = mock<ListenerRegistration>()

        val testAisle = Aisle(
            name = "New Aisle",
            id = "newId",
            timestamp = System.currentTimeMillis()
        )

        // Mock Firestore setup
        `when`(mockFirestore.collection("aisles")).thenReturn(mockCollection)
        `when`(mockCollection.orderBy("timestamp", Query.Direction.ASCENDING)).thenReturn(mockQuery)
        `when`(mockQuery.addSnapshotListener(any<EventListener<QuerySnapshot>>())).thenReturn(mockListenerRegistration)
        `when`(mockCollection.document(testAisle.id)).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.set(testAisle)).thenReturn(mockTask)
        `when`(mockTask.addOnSuccessListener(any())).thenReturn(mockTask) // Chainable Task
        `when`(mockTask.addOnFailureListener(any())).thenReturn(mockTask) // Chainable Task

        // Initialize repository
        val repository = AisleRepository(mockFirestore)

        // Act
        repository.addAisle(testAisle)

        // Assert
        verify(mockCollection).document(testAisle.id)
        verify(mockDocumentReference).set(testAisle)
    }

}