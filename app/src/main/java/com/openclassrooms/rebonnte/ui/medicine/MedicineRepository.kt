package com.openclassrooms.rebonnte.ui.medicine

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MedicineRepository(private val firestore: FirebaseFirestore) {
    private val _medicines = MutableStateFlow<List<Medicine>>(emptyList())
    val medicines: StateFlow<List<Medicine>> = _medicines.asStateFlow()
    private val MEDICINES_COLLECTION = "medicines"

    init {
        loadMedicines() // Default load (unsorted)
    }

    private fun loadMedicines() {
        firestore.collection(MEDICINES_COLLECTION)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Firestore error: ${e.message}")
                    return@addSnapshotListener
                }
                val medList = snapshot?.documents?.mapNotNull { it.toObject(Medicine::class.java) } ?: emptyList()
                _medicines.value = medList
            }
    }

    fun addMedicine(medicine: Medicine) {
        firestore.collection(MEDICINES_COLLECTION)
            .document(medicine.id)
            .set(medicine)
            .addOnSuccessListener { println("Medicine added: ${medicine.name}") }
            .addOnFailureListener { e -> println("Failed to add medicine: ${e.message}") }
    }

    fun updateMedicine(medicine: Medicine) {
        firestore.collection(MEDICINES_COLLECTION)
            .document(medicine.id)
            .set(medicine)
            .addOnSuccessListener { println("Medicine updated: ${medicine.name}") }
            .addOnFailureListener { e -> println("Failed to update medicine: ${e.message}") }
    }

    fun deleteMedicine(medicineId: String) {
        firestore.collection(MEDICINES_COLLECTION)
            .document(medicineId)
            .delete()
            .addOnSuccessListener { println("Medicine deleted: $medicineId") }
            .addOnFailureListener { e -> println("Failed to delete medicine: ${e.message}") }
    }

    // Filter by name using Firestore query
    fun filterByName(name: String) {
        if (name.isEmpty()) {
            loadMedicines() // Reset to all medicines
        } else {
            firestore.collection(MEDICINES_COLLECTION)
                .whereGreaterThanOrEqualTo("name", name)
                .whereLessThanOrEqualTo("name", name + "\uf8ff")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        println("Firestore filter error: ${e.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot == null || snapshot.isEmpty) {
                        println("FirestoreDebug: No matching medicines found for query: $name")
                    } else {
                        println("FirestoreDebug: Query returned ${snapshot.documents.size} documents")
                        for (document in snapshot.documents) {
                            println("FirestoreDebug: Document Data: ${document.data}")
                        }
                    }

                    val medList = snapshot?.documents?.mapNotNull { it.toObject(Medicine::class.java) } ?: emptyList()
                    println("Filtered medicines: ${medList.size}")
                    _medicines.value = medList
                }
        }
    }


    // Sort by name using Firestore query
    fun sortByName() {
        firestore.collection(MEDICINES_COLLECTION)
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Firestore sort error: ${e.message}")
                    return@addSnapshotListener
                }
                val medList = snapshot?.documents?.mapNotNull { it.toObject(Medicine::class.java) } ?: emptyList()
                _medicines.value = medList
            }
    }

    // Sort by stock using Firestore query
    fun sortByStock() {
        firestore.collection(MEDICINES_COLLECTION)
            .orderBy("stock", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Firestore sort error: ${e.message}")
                    return@addSnapshotListener
                }
                val medList = snapshot?.documents?.mapNotNull { it.toObject(Medicine::class.java) } ?: emptyList()
                _medicines.value = medList
            }
    }

    // Reset to unsorted (default)
    fun sortByNone() {
        loadMedicines()
    }
}