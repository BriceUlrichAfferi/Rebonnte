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
import com.openclassrooms.rebonnte.ui.history.History
import com.openclassrooms.rebonnte.ui.medicine.Medicine
import com.openclassrooms.rebonnte.ui.medicine.MedicineRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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

class MedicineRepositoryTest {

    private lateinit var repository: MedicineRepository
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var collection: CollectionReference
    private lateinit var mockQuery: Query

    @Before
    fun setup() {
        mockFirestore = mock(FirebaseFirestore::class.java)
        collection = mock(CollectionReference::class.java)
        mockQuery = mock(Query::class.java)

        `when`(mockFirestore.collection("medicines")).thenReturn(collection)
        repository = MedicineRepository(mockFirestore)
    }

    @After
    fun tearDown() {
        Mockito.framework().clearInlineMocks()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `loadMedicines returns medicines from Firestore`() = runTest {
        // Arrange
        val mockFirestore = mock<FirebaseFirestore>()
        val mockCollection = mock<CollectionReference>()
        val mockSnapshot = mock<QuerySnapshot>()
        val mockDocument = mock<DocumentSnapshot>()
        val mockListenerRegistration = mock<ListenerRegistration>()

        val testHistory = History(
            medicineName = "Aspirin",
            userId = "user123",
            date = "2023-10-01",
            details = "Dispensed 10 units"
        )

        val testMedicine = Medicine(
            name = "Aspirin",
            stock = 50,
            nameAisle = "Pain Relief",
            histories = listOf(testHistory),
            id = "med1",
            addedByEmail = "user@example.com"
        )

        // Mock Firestore setup
        `when`(mockFirestore.collection("medicines")).thenReturn(mockCollection)
        `when`(mockDocument.id).thenReturn("med1") // Mock document ID
        `when`(mockDocument.toObject(Medicine::class.java)).thenReturn(testMedicine)
        `when`(mockSnapshot.documents).thenReturn(listOf(mockDocument))

        var listener: EventListener<QuerySnapshot>? = null
        `when`(mockCollection.addSnapshotListener(any<EventListener<QuerySnapshot>>())).thenAnswer { invocation ->
            listener = invocation.arguments[0] as EventListener<QuerySnapshot>
            mockListenerRegistration
        }

        // Initialize repository
        val repository = MedicineRepository(mockFirestore)

        // Act: Simulate snapshot event
        listener?.onEvent(mockSnapshot, null)

        // Assert
        val result = repository.medicines.first()
        assertEquals(1, result.size)
        assertEquals("Aspirin", result[0].name)
        assertEquals(50, result[0].stock)
        assertEquals("Pain Relief", result[0].nameAisle)
        assertEquals(1, result[0].histories.size)
        assertEquals("Aspirin", result[0].histories[0].medicineName)
        assertEquals("user123", result[0].histories[0].userId)
        assertEquals("2023-10-01", result[0].histories[0].date)
        assertEquals("Dispensed 10 units", result[0].histories[0].details)
        assertEquals("med1", result[0].id)
        assertEquals("user@example.com", result[0].addedByEmail)
    }

    @Test
    fun `addMedicine adds new medicine to Firestore`() {
        // Arrange
        val mockFirestore = mock<FirebaseFirestore>()
        val mockCollection = mock<CollectionReference>()
        val mockDocumentReference = mock<DocumentReference>()
        val mockTask = mock<Task<Void>>()
        val mockListenerRegistration = mock<ListenerRegistration>()

        val testHistory = History(
            medicineName = "Ibuprofen",
            userId = "user456",
            date = "2023-10-02",
            details = "Added 20 units"
        )

        val testMedicine = Medicine(
            name = "Ibuprofen",
            stock = 30,
            nameAisle = "Pain Relief",
            histories = listOf(testHistory),
            id = "", // ID is empty initially, set by Firestore
            addedByEmail = "user2@example.com"
        )

        // Mock Firestore setup
        `when`(mockFirestore.collection("medicines")).thenReturn(mockCollection)
        `when`(mockCollection.addSnapshotListener(any<EventListener<QuerySnapshot>>())).thenReturn(mockListenerRegistration)
        `when`(mockCollection.document()).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.id).thenReturn("med2") // Mock the generated ID
        `when`(mockDocumentReference.set(any())).thenReturn(mockTask)
        `when`(mockTask.addOnSuccessListener(any())).thenReturn(mockTask)
        `when`(mockTask.addOnFailureListener(any())).thenReturn(mockTask)

        val repository = MedicineRepository(mockFirestore)

        // Act
        repository.addMedicine(testMedicine)

        // Assert
        verify(mockCollection).document()

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `filterByName updates medicines flow with filtered results`() = runTest {
        val mockSnapshot = mock(QuerySnapshot::class.java)
        val mockDocument = mock(DocumentSnapshot::class.java)
        val mockListenerRegistration = mock(ListenerRegistration::class.java)

        val testHistory = History(
            medicineName = "Paracetamol",
            userId = "user789",
            date = "2023-10-03",
            details = "Dispensed 5 units"
        )

        val testMedicine = Medicine(
            name = "Paracetamol",
            stock = 20,
            nameAisle = "Fever",
            histories = listOf(testHistory),
            id = "med3",
            addedByEmail = "user3@example.com"
        )

        `when`(mockDocument.toObject(Medicine::class.java)).thenReturn(testMedicine)
        `when`(mockSnapshot.documents).thenReturn(listOf(mockDocument))

        `when`(collection.whereGreaterThanOrEqualTo("name", "Paracetamol")).thenReturn(mockQuery)
        `when`(mockQuery.whereLessThanOrEqualTo("name", "Paracetamol\uF8FF")).thenReturn(mockQuery)

        var listener: EventListener<QuerySnapshot>? = null
        `when`(mockQuery.addSnapshotListener(any<EventListener<QuerySnapshot>>())).thenAnswer { invocation ->
            listener = invocation.arguments[0] as EventListener<QuerySnapshot>
            mockListenerRegistration
        }

        repository.filterByName("Paracetamol")
        listener?.onEvent(mockSnapshot, null)

        val result = repository.medicines.first()
        assertEquals(1, result.size)
        assertEquals("Paracetamol", result[0].name)
        assertEquals(20, result[0].stock)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `sortByName updates medicines flow with sorted results`() = runTest {
        val mockSnapshot = mock(QuerySnapshot::class.java)
        val mockDocument = mock(DocumentSnapshot::class.java)
        val mockListenerRegistration = mock(ListenerRegistration::class.java)

        val testHistory = History(
            medicineName = "Zyrtec",
            userId = "user101",
            date = "2023-10-04",
            details = "Added 15 units"
        )

        val testMedicine = Medicine(
            name = "Zyrtec",
            stock = 10,
            nameAisle = "Allergy",
            histories = listOf(testHistory),
            id = "med4",
            addedByEmail = "user4@example.com"
        )

        `when`(mockDocument.toObject(Medicine::class.java)).thenReturn(testMedicine)
        `when`(mockSnapshot.documents).thenReturn(listOf(mockDocument))

        `when`(collection.orderBy("name", Query.Direction.ASCENDING)).thenReturn(mockQuery)

        var listener: EventListener<QuerySnapshot>? = null
        `when`(mockQuery.addSnapshotListener(any<EventListener<QuerySnapshot>>())).thenAnswer { invocation ->
            listener = invocation.arguments[0] as EventListener<QuerySnapshot>
            mockListenerRegistration
        }

        repository.sortByName()
        listener?.onEvent(mockSnapshot, null)

        val result = repository.medicines.first()
        assertEquals(1, result.size)
        assertEquals("Zyrtec", result[0].name)
        assertEquals(10, result[0].stock)
    }

    @Test
    fun `deleteMedicine removes medicine from Firestore`() {
        val medicineId = "med5"
        val mockDocumentReference = mock(DocumentReference::class.java)
        val mockTask = mock<Task<Void>>()

        `when`(collection.document(medicineId)).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.delete()).thenReturn(mockTask)
        `when`(mockTask.addOnSuccessListener(any())).thenReturn(mockTask)
        `when`(mockTask.addOnFailureListener(any())).thenReturn(mockTask)

        repository.deleteMedicine(medicineId)

        Mockito.verify(collection).document(medicineId)
        Mockito.verify(mockDocumentReference).delete()
    }
}