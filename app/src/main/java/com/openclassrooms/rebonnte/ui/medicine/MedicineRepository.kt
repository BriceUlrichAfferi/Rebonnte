package com.openclassrooms.rebonnte.ui.medicine

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow



class MedicineRepository(private val firestore: FirebaseFirestore) {
    private val _medicines = MutableStateFlow<List<Medicine>>(emptyList())
    val medicines: StateFlow<List<Medicine>> = _medicines.asStateFlow()
    private val MEDICINES_COLLECTION = "medicines"
    private var listenerRegistration: ListenerRegistration? = null

    init {
        loadMedicines()
    }

    fun loadMedicines() {
        listenerRegistration?.remove()
        println("Attaching new medicine snapshot listener")
        listenerRegistration = firestore.collection(MEDICINES_COLLECTION)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Medicine snapshot listener error: ${e.message}")
                    return@addSnapshotListener
                }
                if (snapshot == null) {
                    println("Medicine snapshot is null")
                    return@addSnapshotListener
                }
                val medList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Medicine::class.java)?.copy(id = doc.id)
                }
                println("Medicines updated: $medList")
                _medicines.value = medList
            }
    }

    fun addMedicine(medicine: Medicine) {
        val newDocRef = firestore.collection(MEDICINES_COLLECTION).document()
        val medicineWithId = medicine.copy(id = newDocRef.id)
        newDocRef
            .set(medicineWithId)
            .addOnSuccessListener { println("Medicine added: ${medicineWithId.name}, ID: ${medicineWithId.id}") }
            .addOnFailureListener { e -> println("Failed to add medicine: ${e.message}") }
    }

    fun updateMedicine(medicine: Medicine) {
        if (medicine.id.isEmpty()) {
            println("Error: Cannot update medicine with empty ID")
            return
        }
        println("Updating Medicine: ${medicine.name}, Stock: ${medicine.stock}, Histories: ${medicine.histories}")
        firestore.collection(MEDICINES_COLLECTION)
            .document(medicine.id)
            .set(medicine)
            .addOnSuccessListener {
                loadMedicines()
            }
            .addOnFailureListener { e -> println("Failed to update medicine: ${e.message}") }
    }

    fun deleteMedicine(medicineId: String) {
        firestore.collection(MEDICINES_COLLECTION)
            .document(medicineId)
            .delete()
            .addOnSuccessListener { println("Medicine deleted: $medicineId") }
            .addOnFailureListener { e -> println("Failed to delete medicine: ${e.message}") }
    }

    fun filterByName(name: String) {
        if (name.isEmpty()) {
            loadMedicines()
        } else {
            listenerRegistration?.remove()
            listenerRegistration = firestore.collection(MEDICINES_COLLECTION)
                .whereGreaterThanOrEqualTo("name", name)
                .whereLessThanOrEqualTo("name", name + "\uf8ff")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    val medList = snapshot?.documents?.mapNotNull { it.toObject(Medicine::class.java) } ?: emptyList()
                    _medicines.value = medList
                }
        }
    }

    fun sortByName() {
        listenerRegistration?.remove()
        listenerRegistration = firestore.collection(MEDICINES_COLLECTION)
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

    fun sortByStock() {
        listenerRegistration?.remove()
        listenerRegistration = firestore.collection(MEDICINES_COLLECTION)
            .orderBy("stock", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                val medList = snapshot?.documents?.mapNotNull { it.toObject(Medicine::class.java) } ?: emptyList()
                _medicines.value = medList
            }
    }

    fun sortByNone() {
        loadMedicines()
    }
}